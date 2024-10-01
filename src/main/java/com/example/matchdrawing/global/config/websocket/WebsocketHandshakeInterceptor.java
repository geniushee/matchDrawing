package com.example.matchdrawing.global.config.websocket;

import com.example.matchdrawing.domain.member.member.entity.Member;
import com.example.matchdrawing.domain.member.member.service.MemberService;
import com.example.matchdrawing.domain.service.DrawingService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * interceptor를 사용하고 싶다면 config에 등록을 해야한다.
 */
@Component
@RequiredArgsConstructor
public class WebsocketHandshakeInterceptor implements HandshakeInterceptor {
    private final MemberService memberService;
    @Autowired
    @Lazy
    private DrawingService drawingService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("핸드쉐이크 전처리");
        System.out.println("사용자 정보 처리");
        Member member = null;

        // 사용자 정보 처리
        List<String> cookieheader = request.getHeaders().get("Cookie");
        String[] cookieString = new String[0];
        if (cookieheader != null && !cookieheader.isEmpty()) {
            cookieString = cookieheader.getFirst().split("; ");
        }

        if (cookieString.length != 0) {

            System.out.println("cookieString : " + Arrays.toString(cookieString));
            List<Cookie> cookies = Arrays.stream(cookieString).map(str -> {
                String[] arr = str.split("=", 2);
                return new Cookie(arr[0], arr[1]);
            }).toList();

            Cookie loginCookie = cookies.stream().filter(cookie -> cookie.getName().equals("login")).findFirst()
                    .orElse(null);

            if (loginCookie != null) {
                Optional<Member> opMember = memberService.findByUsername(loginCookie.getValue());
                if (opMember.isPresent()) {
                    member = opMember.get();
                    attributes.put("user", member);
                }
            }


        }

        System.out.println("대기실 접속 처리");
        // 대기실 접속 처리
        String query = request.getURI().getQuery();
//        System.out.println("쿼리 확인");쿼리 확인
//        System.out.println(query);roomId=3

        String[] querys = query.split("&");
        Map<String, String> params = Arrays.stream(querys).map(q -> q.split("="))
                .collect(Collectors.toMap(
                        pair -> pair[0],
                        pair -> pair.length > 1 ? pair[1] : ""));

        String roomId = params.get("roomId");
        if (!roomId.isEmpty()) {
            attributes.put("roomId", roomId);
            if (member != null) {
                drawingService.enterWaitingRoom(Long.valueOf(roomId), member);
                System.out.println("한명 추가");
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}

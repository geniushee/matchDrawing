package com.example.matchdrawing.global.config.websocket.config;

import com.example.matchdrawing.domain.member.member.entity.Member;
import com.example.matchdrawing.domain.member.member.service.MemberService;
import com.example.matchdrawing.domain.game.game.service.DrawingService;
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
 * 채팅기능을 하는 웹소켓의 인터셉터.
 * 인터셉터는 핸드쉐이크 할때 동작함.
 * 그림판과는 분리하여 작성했으며 채팅기능에는 사용자정보를 포함하도록 함.
 */
@Component
@RequiredArgsConstructor
public class WebsocketHandshakeInterceptor implements HandshakeInterceptor {
    private final MemberService memberService;
    @Autowired
    @Lazy
    private DrawingService drawingService;

    /**
     * 웹소켓에 사용자 정보를 주입하기 위한 핸드쉐이커
     * @param request the current request
     * @param response the current response
     * @param wsHandler the target WebSocket handler
     * @param attributes the attributes from the HTTP handshake to associate with the WebSocket
     * session; the provided attributes are copied, the original map is not used.
     * @return true
     * @throws Exception 사용자가 로그인을 하지 않아 토큰이 없을 때.
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 초기화
        Member member = null;

        // 사용자 정보 처리
        List<String> cookieheader = request.getHeaders().get("Cookie");
        String[] cookieString = new String[0];
        if (cookieheader != null && !cookieheader.isEmpty()) {
            cookieString = cookieheader.getFirst().split("; ");
        }


        if (cookieString.length != 0) {
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
                    // 사용자 정보 주입
                    attributes.put("user", member);
                    // 채팅 웹소켓 표기
                    attributes.put("type", "msg");
                }
            }

            // 대기실 접속 처리, 쿼리로 게임방을 특정
            String query = request.getURI().getQuery();

            String[] querys = query.split("&");
            Map<String, String> params = Arrays.stream(querys).map(q -> q.split("="))
                    .collect(Collectors.toMap(
                            pair -> pair[0],
                            pair -> pair.length > 1 ? pair[1] : ""));

            String roomId = params.get("roomId");
            if (!roomId.isEmpty()) {
                attributes.put("roomId", roomId);
                if (member != null) {
                    // 방정보에 사용자 추가
                    drawingService.enterWaitingRoom(Long.valueOf(roomId), member);
                }
            }
        }else {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}

package com.example.matchdrawing.global.config.websocket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * interceptor를 사용하고 싶다면 config에 등록을 해야한다.
 * 그림판과 통신하는 웹소켓 인터셉터. 웹소켓 연결종료 이벤트를 사용한 이벤트 처리메소드가 있어. 채팅기능과 분리를 위해 attribute 작성.
 */
@Component
@RequiredArgsConstructor
public class WebsocketDrawingHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("핸드쉐이크 전처리");
        attributes.put("type", "dr");
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}

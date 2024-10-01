package com.example.matchdrawing.global.config.websocket;

import com.example.matchdrawing.domain.member.member.entity.Member;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    /**
     * security를 사용하지 않아서 Principal의 커스텀 구현체를 사용
     * security를 사용했다면 UsernamePasswordAuthenticationToken을 사용
     * @param request the handshake request
     * @param wsHandler the WebSocket handler that will handle messages
     * @param attributes handshake attributes to pass to the WebSocket session
     * @return
     */
    @Override
    public Principal determineUser(
            ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Member member = (Member) attributes.get("user");
        return new CustomPrincipal(member);
    }
}

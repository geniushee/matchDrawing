package com.example.matchdrawing.global.config.websocket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Profile("websocket")
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class SimpleWebsocketConfig implements WebSocketMessageBrokerConfigurer {

    private final CustomHandshakeHandler customHandshakeHandler;
    private final WebsocketHandshakeInterceptor msgHandshakeInterceptor;
    private final WebsocketDrawingHandshakeInterceptor drawingHandshakeInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("localhost")
                .addInterceptors(msgHandshakeInterceptor)
                .setHandshakeHandler(customHandshakeHandler)
                .withSockJS();

        registry.addEndpoint("/dr")
                .addInterceptors(drawingHandshakeInterceptor)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(64 * 1024);
    }
}

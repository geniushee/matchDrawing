package com.example.matchdrawing.global.config.websocket.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleStompTemplate implements StompTemplate {
    private final SimpMessagingTemplate template;

    @Override
    public void convertAndSend(String destination, MessageDto messageDto){
        String dest = "/topic" + destination;
        template.convertAndSend(dest, messageDto);
    }

}

package com.example.matchdrawing.global.config.websocket.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 웹소켓에 메세지를 보내기 위한 클래스.
 * 스프링에서 기본지원하는 simpleMessageBroker에 대응.
 */

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

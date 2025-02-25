package com.example.matchdrawing.global.config.websocket.stompTemplate;

import com.example.matchdrawing.global.config.websocket.message.Message;

/**
 * 외부 브로커로 확장 가능성을 고려한 인터페이스
 */
public interface StompTemplate {

    void convertAndSend(String destination, Message message);
}

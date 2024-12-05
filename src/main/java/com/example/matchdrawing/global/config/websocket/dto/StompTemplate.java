package com.example.matchdrawing.global.config.websocket.dto;

/**
 * 외부 브로커로 확장 가능성을 고려한 인터페이스
 */
public interface StompTemplate {

    void convertAndSend(String destination, MessageDto dto);
}

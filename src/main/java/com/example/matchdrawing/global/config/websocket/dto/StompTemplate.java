package com.example.matchdrawing.global.config.websocket.dto;


public interface StompTemplate {

    void convertAndSend(String destination, MessageDto dto);
}

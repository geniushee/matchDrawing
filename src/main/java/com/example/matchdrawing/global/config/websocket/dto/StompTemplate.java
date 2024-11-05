package com.example.matchdrawing.global.config.websocket.dto;


import com.example.matchdrawing.global.dto.MessageDto;

public interface StompTemplate {

    void convertAndSend(String destination, MessageDto dto);
}

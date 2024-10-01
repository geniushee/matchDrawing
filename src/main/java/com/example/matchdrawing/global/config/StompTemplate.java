package com.example.matchdrawing.global.config;


import com.example.matchdrawing.global.dto.SimpleMessageDto;

public interface StompTemplate {

    void convertAndSend(String destination, SimpleMessageDto dto);
}

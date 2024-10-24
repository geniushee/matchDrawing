package com.example.matchdrawing.global.config;


import com.example.matchdrawing.global.dto.MessageDto;

public interface StompTemplate {

    void convertAndSend(String destination, MessageDto dto);
}

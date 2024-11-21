package com.example.matchdrawing.global.config.websocket.dto;

import lombok.Data;

@Data
public class SimpleMessageDto extends MessageDto {
    private String sender;
}

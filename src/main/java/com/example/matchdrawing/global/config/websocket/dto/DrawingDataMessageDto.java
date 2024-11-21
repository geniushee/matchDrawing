package com.example.matchdrawing.global.config.websocket.dto;

import lombok.Data;

@Data
public class DrawingDataMessageDto extends MessageDto {
    private String sender;
}

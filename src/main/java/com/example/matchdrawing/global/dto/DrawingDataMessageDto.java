package com.example.matchdrawing.global.dto;

import lombok.Data;

@Data
public class DrawingDataMessageDto implements MessageDto{
    private String jsonData;
    private String sender;
}

package com.example.matchdrawing.global.dto;

import lombok.Data;

@Data
public class SimpleMessageDto implements MessageDto{
    private String sender;
    private String msg;
}

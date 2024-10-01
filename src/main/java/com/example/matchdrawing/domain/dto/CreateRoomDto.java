package com.example.matchdrawing.domain.dto;

import lombok.Data;

@Data
public class CreateRoomDto {
    private String roomName;
    private int numOfParticipants;
}

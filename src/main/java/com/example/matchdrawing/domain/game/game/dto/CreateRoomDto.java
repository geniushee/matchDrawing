package com.example.matchdrawing.domain.game.game.dto;

import lombok.Data;

@Data
public class CreateRoomDto {
    private String roomName;
    private int numOfParticipants;
    private int countOfAnswers;
}

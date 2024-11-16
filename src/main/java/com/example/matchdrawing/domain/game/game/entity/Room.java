package com.example.matchdrawing.domain.game.game.entity;

public interface Room {

    public RoomStatus getType();

    void changeStatus(String status);
}

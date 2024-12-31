package com.example.matchdrawing.domain.game.game.dto;

import com.example.matchdrawing.domain.game.game.entity.LoadingRoom;
import com.example.matchdrawing.domain.game.game.entity.RoomStatus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LoadingRoomDto {
    private Long id;
    private Long roomId;
    private List<String> usernames = new ArrayList<>();
    private RoomStatus type;

    public LoadingRoomDto(LoadingRoom entity){
        this   .id = entity.getId();
        this.roomId = entity.getRoom().getId();
        entity.getCompleteMember()
                .forEach(member ->
                        usernames.add(member.getUsername()));
        this.type = entity.getType();
    }
}

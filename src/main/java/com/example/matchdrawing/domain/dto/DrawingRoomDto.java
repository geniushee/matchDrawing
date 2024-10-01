package com.example.matchdrawing.domain.dto;

import com.example.matchdrawing.domain.entity.DrawingRoom;
import com.example.matchdrawing.domain.member.member.dto.MemberDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class DrawingRoomDto {
    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
    private String roomName;
    private List<MemberDto> curMember;
    private int numOfParticipants;
    private int numOfCurParticipants;

    public DrawingRoomDto(DrawingRoom entity){
        this.id = entity.getId();
        this.createTime = entity.getCreateTime();
        this.modifyTime = entity.getModifyTime();
        this.roomName = entity.getRoomName();
        this.curMember = entity.getCurMember().stream()
                .map(MemberDto::new).toList();
        this.numOfParticipants = entity.getNumOfParticipants();
        this.numOfCurParticipants = entity.getCurMember().size();
    }
}

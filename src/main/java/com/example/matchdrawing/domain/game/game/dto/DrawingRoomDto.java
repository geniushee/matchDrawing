package com.example.matchdrawing.domain.game.game.dto;

import com.example.matchdrawing.domain.game.game.entity.DrawingRoom;
import com.example.matchdrawing.domain.game.game.entity.RoomStatus;
import com.example.matchdrawing.domain.member.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
public class DrawingRoomDto {
    private Long id;
    private RoomStatus status;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
    private String roomName;
    private MemberDto owner;
    private List<MemberDto> curMember;
    private int numOfParticipants;
    private int numOfCurParticipants;
    private List<AnswerDto> answers;

    public DrawingRoomDto(DrawingRoom entity){
        this.id = entity.getId();
        this.status = entity.getType();
        this.createTime = entity.getCreateTime();
        this.modifyTime = entity.getModifyTime();
        this.roomName = entity.getRoomName();
        this.owner = new MemberDto(entity.getOwner());
        this.curMember = entity.getCurMember().stream()
                .map(MemberDto::new).toList();
        this.answers = entity.getAnswers().stream()
                .map(AnswerDto::new).toList();
        this.numOfParticipants = entity.getNumOfParticipants();
        this.numOfCurParticipants = entity.getCurMember().size();
    }
}

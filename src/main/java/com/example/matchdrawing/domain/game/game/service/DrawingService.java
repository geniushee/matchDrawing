package com.example.matchdrawing.domain.game.game.service;

import com.example.matchdrawing.domain.game.game.dto.DrawingRoomDto;
import com.example.matchdrawing.domain.game.game.entity.DrawingRoom;
import com.example.matchdrawing.domain.game.game.repository.DrawingRoomRepository;
import com.example.matchdrawing.domain.member.member.entity.Member;
import com.example.matchdrawing.global.config.StompTemplate;
import com.example.matchdrawing.global.dto.MessageDto;
import com.example.matchdrawing.global.dto.SimpleMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrawingService {

    private final DrawingRoomRepository drawingRoomRepository;
    private final StompTemplate template;


    @Transactional
    public DrawingRoomDto createRoom(String roomName, int numOfParticipant) {
        DrawingRoom room = DrawingRoom.builder()
                .roomName(roomName)
                .numOfParticipants(numOfParticipant)
                .build();

        room = drawingRoomRepository.save(room);
        return new DrawingRoomDto(room);
    }

    public Page<DrawingRoomDto> getRoomList(Pageable pageable) {

        Page<DrawingRoom> data = drawingRoomRepository.findAll(pageable);
        List<DrawingRoomDto> dtos = data.stream().map(DrawingRoomDto::new).toList();
        return new PageImpl<>(dtos, pageable, data.getTotalElements());
    }


    public void sendMessage(String destination, MessageDto msgDto) {
        template.convertAndSend(destination, msgDto);
    }


    public boolean checkEventStartGame(SimpleMessageDto msgDto) {
        if(msgDto.getMsg().contains("Event:/")){
            String msg = msgDto.getMsg().replace("Event:/","");
            return msg.equals("startGame");
        }
        return false;
    }

    public DrawingRoomDto findById(Long roomId) {
        DrawingRoom room = drawingRoomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("잘못된 방입니다."));
        return new DrawingRoomDto(room);
    }

    public Long countRooms() {
        return drawingRoomRepository.count();
    }

    @Transactional
    public void enterWaitingRoom(Long id, Member member) {
        DrawingRoom room = drawingRoomRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 방입니다."));
        if (!room.isMax() && !room.getCurMember().contains(member)) {
            room.enterMember(member);
        } else {
            return;
        }
        drawingRoomRepository.save(room);
    }

    @Transactional
    public void exitRoom(Long id, Member member) {
        DrawingRoom room = drawingRoomRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 방입니다."));

        System.out.println("들어있나 확인 : " + room.containMember(member));
        if(room.containMember(member)){
            System.out.println(member.getName() + "방나가기");
            room.exitMember(member);
            System.out.println(room.getCurMember() + " - 현재 방 참가자");
        }

        System.out.println("비어있나 확인 : " + room.isEmpty());
        if(room.isEmpty()){
            System.out.println("방 제거");
            drawingRoomRepository.delete(room);
        }
    }

}

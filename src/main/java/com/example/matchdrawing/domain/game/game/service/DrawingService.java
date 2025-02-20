package com.example.matchdrawing.domain.game.game.service;

import com.example.matchdrawing.domain.game.game.dto.DrawingRoomDto;
import com.example.matchdrawing.domain.game.game.dto.LoadingRoomDto;
import com.example.matchdrawing.domain.game.game.entity.Answer;
import com.example.matchdrawing.domain.game.game.entity.DrawingRoom;
import com.example.matchdrawing.domain.game.game.entity.LoadingRoom;
import com.example.matchdrawing.domain.game.game.entity.RoomStatus;
import com.example.matchdrawing.domain.game.game.repository.DrawingRoomRepository;
import com.example.matchdrawing.domain.game.game.repository.LoadingRoomRepository;
import com.example.matchdrawing.domain.member.member.entity.Member;
import com.example.matchdrawing.domain.member.member.service.MemberService;
import com.example.matchdrawing.global.config.websocket.dto.MessageDto;
import com.example.matchdrawing.global.config.websocket.dto.SimpleMessageDto;
import com.example.matchdrawing.global.config.websocket.dto.StompTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.matchdrawing.domain.game.game.entity.RoomStatus.LOADING;
import static com.example.matchdrawing.domain.game.game.entity.RoomStatus.WAITING;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrawingService {

    private final DrawingRoomRepository drawingRoomRepository;
    private final LoadingRoomRepository loadingRoomRepository;
    private final StompTemplate template;
    private final MemberService memberService;
    private final AnswerService answerService;


    @Transactional
    public DrawingRoomDto createRoom(String username, String roomName, int numOfParticipant, int countOfAnswers) {
        Member user = memberService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        List<Answer> answers = answerService.createAnswerList(countOfAnswers);
        DrawingRoom room = DrawingRoom.builder()
                .type(WAITING)
                .roomName(roomName)
                .owner(user)
                .numOfParticipants(numOfParticipant)
                .answers(answers)
                .build();

        room = drawingRoomRepository.save(room);
//        enterWaitingRoom(room.getId(), user);
        createLoadingRoom(room.getId());

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

    public DrawingRoomDto findRoomDtoById(Long roomId) {
        return new DrawingRoomDto(findById(roomId));
    }

    public Long countRooms() {
        return drawingRoomRepository.count();
    }

    @Transactional
    public void enterWaitingRoom(Long id, Member member) {
        DrawingRoom room = drawingRoomRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 방입니다."));
        if (!room.isMax()){
            List<Member> curMember = room.getCurMember();
            Optional<Member> opMember = Optional.empty();
            for(Member mem : curMember){
                if(mem.getId().equals(member.getId())){
                    opMember = Optional.of(mem);
                }
            }
            if(opMember.isEmpty()) {
                room.enterMember(member);
            }
        } else {
            return;
        }
        drawingRoomRepository.save(room);
    }

    @Transactional
    public void exitRoom(Long id, Member member) {
        DrawingRoom room = findById(id);

        System.out.println("들어있나 확인 : " + room.containMember(member));
        if(room.containMember(member)){
            System.out.println(member.getName() + "방나가기");
            room.exitMember(member);
            System.out.println(room.getCurMember() + " - 현재 방 참가자");
        }

        System.out.println("비어있나 확인 : " + room.isEmpty());
        if(room.isEmpty()){
            System.out.println("방 제거");
            breakRoom(room);
        }
    }

    @Transactional
    public void breakRoom(DrawingRoom room){
        deleteLoadingRoom(room.getId());
        drawingRoomRepository.delete(room);
    }

    @Transactional
    public void breakRoom(Long roomId){
        breakRoom(findById(roomId));
    }

    public boolean isOwner(Long roomId, Member member) {
        DrawingRoom room = findById(roomId);
        return room.isOwner(member);
    }

    private DrawingRoom findById(Long id){
        return drawingRoomRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 방입니다."));
    }

    public boolean isNotLoading(Long id) {
        RoomStatus status =  findById(id).getType();
        return status != LOADING;
    }

    @Transactional
    public void changeRoomStatus(Long id, String status) {
        DrawingRoom room = findById(id);
        LoadingRoom loadingRoom = loadingRoomRepository.findByRoomId(id).get();
        room.changeStatus(status);
        loadingRoom.changeStatus(status);
    }

    @Transactional
    public void createLoadingRoom(Long id) {
        LoadingRoom loadingRoom = LoadingRoom.builder()
                .room(findById(id))
                .type(LOADING)
                .build();
        loadingRoomRepository.save(loadingRoom);
    }

    public boolean checkLoadingByRoomId(Long roomId) {
        LoadingRoom loading = loadingRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("로딩룸을 찾을 수 없습니다."));
        return loading.loadingComplete();
    }


    @Transactional
    public boolean enterGame(Long roomId, String username) {
        DrawingRoom room = findById(roomId);
        LoadingRoom loading = loadingRoomRepository.findByRoom(room);
        Member member = memberService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if(room.getCurMember().contains(member)){
            loading.addMember(member);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteLoadingRoom(Long roomId) {
        Optional<LoadingRoom> opLr = loadingRoomRepository.findByRoomId(roomId);
        opLr.ifPresent(loadingRoomRepository::delete);
    }

    public LoadingRoomDto findLoadingRoomDtoByRoomId(Long roomId){
        Optional<LoadingRoom> opLr = loadingRoomRepository.findByRoomId(roomId);
        return opLr.map(LoadingRoomDto::new).orElse(null);
    }
}

package com.example.matchdrawing.domain.game.game.contorller;

import com.example.matchdrawing.domain.game.game.dto.CreateRoomDto;
import com.example.matchdrawing.domain.game.game.dto.DrawingRoomDto;
import com.example.matchdrawing.domain.game.game.service.DrawingService;
import com.example.matchdrawing.global.config.websocket.dto.CustomPrincipal;
import com.example.matchdrawing.global.config.websocket.dto.DrawingDataMessageDto;
import com.example.matchdrawing.global.config.websocket.dto.SimpleMessageDto;
import com.example.matchdrawing.global.request.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
@RequestMapping("/roby")
public class DrawingController {

    private final DrawingService drawingService;
    private final Rq rq;

    @GetMapping("")
    public String showRoby(){
        return "home";
    }

    @GetMapping("/list")
    public String showList(){
        return "roomList";
    }

    @GetMapping("/create")
    public String showCreateRoomPage(){
        if(rq.isLogin()){
        return "createRoom";
        }
        String msg = URLEncoder.encode("로그인이 필요합니다.", StandardCharsets.UTF_8);
        return "redirect:/roby?msg="+msg;
    }

    /**
     * 방을 만드는 메소드
     * @param formDto 방이름, 참여 인원수, 문제개수 필요
     * @return 생성된 room으로 redirect
     */
    @PostMapping("/create")
    public String createRoom(@ModelAttribute CreateRoomDto formDto){
       DrawingRoomDto roomDto = drawingService.createRoom(rq.getUsername(), formDto.getRoomName(), formDto.getNumOfParticipants(),
               formDto.getCountOfAnswers());
       return "redirect:/roby/room/" + roomDto.getId();
    }

    @GetMapping("/room/{roomId}")
    public String enterRoom(@PathVariable(name="roomId")Long roomId,
                            Model model){
        if(!rq.isLogin()){
            String msg = URLEncoder.encode("로그인이 필요합니다.", StandardCharsets.UTF_8);
            return "redirect:/member/signin?msg="+msg;
        }

        drawingService.enterWaitingRoom(roomId, rq.getMember());
        DrawingRoomDto roomDto = drawingService.findRoomDtoById(roomId);
        model.addAttribute("roomDto", roomDto);

        String destination = "/room" + roomId;
        SimpleMessageDto msgDto = new SimpleMessageDto();
        msgDto.setSender("system");
        msgDto.setMsg(String.format("%s가 참가했습니다.", rq.getUsername()));
        drawingService.sendMessage(destination, msgDto);

        return "room";
    }

    @MessageMapping("/room{id}")
    public void roomSendMsg(@DestinationVariable(value = "id")Long id,
                            SimpleMessageDto msgDto,
                            CustomPrincipal user,
                            @Qualifier("frontUrl")String frontUrl){
        if(drawingService.checkEventStartGame(msgDto)){
            msgDto.setMsg(frontUrl + "/roby/game/"+id);
            msgDto.setSender("start");
        }else{
            msgDto.setSender(user.getName());
        }
        String destination = "/room" + id;
        drawingService.sendMessage(destination, msgDto);
    }

    @GetMapping("/test")
    public String showTestPage(){
        return "canvas";
    }

    @MessageMapping("/drawing{id}")
    public void drawingSendImg(@DestinationVariable(value = "id")Long id,
                               DrawingDataMessageDto msgDto,
                               CustomPrincipal user){
        msgDto.setSender("시험");
        String destination = "/drawing"+id;
        drawingService.sendMessage(destination, msgDto);
    }

    @GetMapping("/game/{id}")
    public String startGame(@PathVariable(name = "id")Long roomId,
                          Model model){
        DrawingRoomDto dto =  drawingService.findRoomDtoById(roomId);

        model.addAttribute("roomDto", dto);
        return "gameRoom";
    }
}

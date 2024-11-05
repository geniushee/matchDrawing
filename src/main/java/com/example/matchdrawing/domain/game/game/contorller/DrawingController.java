package com.example.matchdrawing.domain.game.game.contorller;

import com.example.matchdrawing.domain.game.game.dto.CreateRoomDto;
import com.example.matchdrawing.domain.game.game.dto.DrawingRoomDto;
import com.example.matchdrawing.domain.game.game.service.DrawingService;
import com.example.matchdrawing.global.Rq;
import com.example.matchdrawing.global.config.websocket.dto.CustomPrincipal;
import com.example.matchdrawing.global.dto.DrawingDataMessageDto;
import com.example.matchdrawing.global.dto.SimpleMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public String showRoomList(Model model) {
        //page 설정
        int pageSize = 5;
        int pageNum = 0;
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(Sort.Order.desc("createTime"));
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(orders));

        Page<DrawingRoomDto> list = drawingService.getRoomList(pageable);
        model.addAttribute("roomList", list);

        return "roomList";

    }

//    @GetMapping("/{id}")
//    public String participateRoom(@PathVariable(name = "id")Long id, Model model){
//        drawingService.getRoom()
//    }

    @GetMapping("/create")
    public String showCreateRoomPage(){
        if(rq.isLogin()){
        return "createRoom";
        }
        return "redirect:/roby";
    }

    @PostMapping("/create")
    public String createRoom(@ModelAttribute CreateRoomDto formDto){
       DrawingRoomDto roomDto = drawingService.createRoom(formDto.getRoomName(), formDto.getNumOfParticipants());
       return "redirect:/roby/room/" + roomDto.getId();
    }

    @GetMapping("/room/{roomId}")
    public String enterRoom(@PathVariable(name="roomId")Long roomId,
                            Model model){
        if(!rq.isLogin()){
            return  "redirect:/member/signin";
        }

        DrawingRoomDto roomDto = drawingService.findById(roomId);
        String destination = "/room" + roomId;
        SimpleMessageDto msgDto = new SimpleMessageDto();
        msgDto.setSender("system");
        msgDto.setMsg(String.format("%s가 참가했습니다.", rq.getUsername()));
        drawingService.sendMessage(destination, msgDto);
        model.addAttribute("roomDto", roomDto);
        return "room";
    }

    @MessageMapping("/room{id}")
    public void roomSendMsg(@DestinationVariable(value = "id")Long id,
                            SimpleMessageDto msgDto,
                            CustomPrincipal user){
        if(drawingService.checkEventStartGame(msgDto)){
            msgDto.setMsg("http://localhost:8080/roby/game/"+id);
            msgDto.setSender("system");
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
        model.addAttribute("roomId", roomId);
        return "gameRoom";
    }
}

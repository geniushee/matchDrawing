package com.example.matchdrawing.domain.game.game.contorller;

import com.example.matchdrawing.domain.game.game.service.DrawingService;
import com.example.matchdrawing.global.config.SiteProperties;
import com.example.matchdrawing.global.config.websocket.dto.CustomPrincipal;
import com.example.matchdrawing.global.config.websocket.dto.DrawingDataMessageDto;
import com.example.matchdrawing.global.config.websocket.dto.SimpleMessageDto;
import com.example.matchdrawing.global.request.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DrawingMsgController {

    private final DrawingService drawingService;
    private final SiteProperties siteProperties;
    private final Rq rq;

    @MessageMapping("/room{id}")
    public void roomSendMsg(@DestinationVariable(value = "id")Long id,
                            SimpleMessageDto msgDto,
                            CustomPrincipal user){
        if(drawingService.checkEventStartGame(msgDto)){
            msgDto.setMsg("/roby/game/"+id);
            msgDto.setSender("start");
        }else{
            msgDto.setSender(user.getName());
        }
        String destination = "/room" + id;
        drawingService.sendMessage(destination, msgDto);
    }

    @MessageMapping("/drawing{id}")
    public void drawingSendImg(@DestinationVariable(value = "id")Long id,
                               DrawingDataMessageDto msgDto,
                               CustomPrincipal user){
        msgDto.setSender(rq.getUsername());
        String destination = "/drawing"+id;
        drawingService.sendMessage(destination, msgDto);
    }
}

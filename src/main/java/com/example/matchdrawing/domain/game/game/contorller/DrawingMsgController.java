package com.example.matchdrawing.domain.game.game.contorller;

import com.example.matchdrawing.domain.game.game.dto.MessageDto;
import com.example.matchdrawing.domain.game.game.service.DrawingService;
import com.example.matchdrawing.global.config.websocket.dto.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DrawingMsgController {

    private final DrawingService drawingService;

    @MessageMapping("/room{id}")
    public void roomSendMsg(@DestinationVariable(value = "id")Long id,
                            MessageDto msgDto,
                            CustomPrincipal user){
        String destination = "/room" + id;
        drawingService.sendMessage(destination,
                msgDto.getType(),
                msgDto.getContent(),
                user.getName(),
                msgDto.getEventType());
    }
}

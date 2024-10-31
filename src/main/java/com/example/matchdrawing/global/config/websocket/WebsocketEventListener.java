package com.example.matchdrawing.global.config.websocket;

import com.example.matchdrawing.domain.member.member.entity.Member;
import com.example.matchdrawing.domain.game.game.service.DrawingService;
import com.example.matchdrawing.global.dto.SimpleMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebsocketEventListener {

    private final DrawingService drawingService;

    @EventListener
    public void handleWebsocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println("이벤트 리스너 작동 확인");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("도착지 확인 : " + headerAccessor);

        Member member = (Member) headerAccessor.getSessionAttributes().get("user");
        System.out.printf("""
                멤버 확인
                id : %d
                이름 : %s
                %n""", member.getId(), member.getUsername());


        Long id = Long.valueOf((String) headerAccessor.getSessionAttributes().get("roomId"));

        //퇴장 메세지
        String destination = "/room" + id;
        SimpleMessageDto msgDto = new SimpleMessageDto();
        msgDto.setSender("system");
        msgDto.setMsg(String.format("%s가 퇴장했습니다.", member.getUsername()));
        drawingService.sendMessage(destination, msgDto);

        //db데이터 변경
        drawingService.exitRoom(id, member);

    }
}

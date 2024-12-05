package com.example.matchdrawing.global.config.websocket.listener;

import com.example.matchdrawing.domain.game.game.service.DrawingService;
import com.example.matchdrawing.domain.member.member.entity.Member;
import com.example.matchdrawing.global.config.websocket.dto.SimpleMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * 웹소켓 이벤트 리스터 Bean등록
 * 웹소켓 연결 종료시 게임방에서 나가는 것을 처리.
 */
@Component
@RequiredArgsConstructor
public class WebsocketEventListener {

    private final DrawingService drawingService;

    /**
     * 연결종료 이벤트를 처리
     * @param event 웹소켓 연결종료 시 발생하는 이벤트
     */
    @EventListener
    public void handleWebsocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String type = (String) headerAccessor.getSessionAttributes().get("type");

        if (type.equals("msg")) {
            Member member = (Member) headerAccessor.getSessionAttributes().get("user");
            Long id = Long.valueOf((String) headerAccessor.getSessionAttributes().get("roomId"));

            // 대기실 및 게임 중 상태에서 나갈경우에만
            if (drawingService.isNotLoading(id)) {
                if (drawingService.isOwner(id, member)) {
                    //방이 없어진다. sender를 break로 하여 break이 경우 모든 참가자들은 메세지와 함꼐 모두 로비로 이동.
                    String destination = "/room" + id;
                    SimpleMessageDto msgDto = new SimpleMessageDto();
                    msgDto.setSender("break");
                    msgDto.setMsg(String.format("방장이 퇴장했습니다."));
                    drawingService.sendMessage(destination, msgDto);
                    drawingService.breakRoom(id);
                } else {
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
        } else if (type.equals("dr")) {
            //그림판의 연결종료는 해당 이벤트가 발생하지 않도록 함.
        }
    }
}

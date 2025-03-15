package com.example.matchdrawing.global.config.websocket.messageHandler;

import com.example.matchdrawing.global.config.websocket.message.EventMessage;
import com.example.matchdrawing.global.config.websocket.message.Message;
import com.example.matchdrawing.global.config.websocket.message.MessageType;
import com.example.matchdrawing.global.config.websocket.stompTemplate.StompTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventMessageHandler implements MessageHandler {
    private final StompTemplate template;

    public EventMessageHandler(StompTemplate template) {
        this.template = template;
    }

    @Override
    public void handle(String destination, Message message) {
        if (message.getType().equals(MessageType.EVENT) && checkEventStartGame(message)) {
            String roomId = destination.replace("/room", "");
            message.setContent("/roby/game/" + roomId);
            message.setSender("start");
        }
        template.convertAndSend(destination, message);
    }

    public boolean checkEventStartGame(Message message) {
        return ((EventMessage) message).getEventType().equals("startGame");
    }
}

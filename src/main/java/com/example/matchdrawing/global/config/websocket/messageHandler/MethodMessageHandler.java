package com.example.matchdrawing.global.config.websocket.messageHandler;

import com.example.matchdrawing.global.config.websocket.message.Message;
import com.example.matchdrawing.global.config.websocket.stompTemplate.StompTemplate;
import org.springframework.stereotype.Component;

@Component
public class MethodMessageHandler implements MessageHandler{
    private final StompTemplate template;

    public MethodMessageHandler(StompTemplate template){
        this.template = template;
    }

    @Override
    public void handle(String destination, Message message) {
        template.convertAndSend(destination, message);
    }
}

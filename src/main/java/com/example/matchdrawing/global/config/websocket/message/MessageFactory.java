package com.example.matchdrawing.global.config.websocket.message;

import org.springframework.stereotype.Component;

@Component
public class MessageFactory {
    public Message createMessage(MessageType type, String content, String sender, String eventType){
        Message message;
        switch(type){
            case MessageType.TEXT -> message = createTextMessage(content, sender);
            case MessageType.METHOD -> message = createMethodMessage(content,sender);
            case MessageType.EVENT -> message = createEventMessage(content, sender, eventType);
            default -> throw new RuntimeException("찾는 메시지 타입이 없습니다.");
        }
        return message;
    }

    public TextMessage createTextMessage(String content, String sender){
        return new TextMessage(content, sender);
    }
    public MethodMessage createMethodMessage(String content, String sender){
        return new MethodMessage(content, sender);
    }
    public EventMessage createEventMessage(String content, String sender, String eventType){
        return new EventMessage(content, sender, eventType);
    }
}

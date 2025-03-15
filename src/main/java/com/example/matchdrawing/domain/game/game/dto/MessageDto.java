package com.example.matchdrawing.domain.game.game.dto;

import com.example.matchdrawing.global.config.websocket.message.Message;
import com.example.matchdrawing.global.config.websocket.message.MessageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto implements Message {
    String type;
    String content;
    String sender;
    String EventType;

    public MessageDto(){}

    public MessageDto(String type, String content, String sender, String eventType){
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.EventType = eventType;
    }

    @Override
    public MessageType getType(){
        return MessageType.valueOf(this.type);
    }
}

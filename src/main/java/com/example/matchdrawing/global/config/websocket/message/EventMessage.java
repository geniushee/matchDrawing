package com.example.matchdrawing.global.config.websocket.message;

import lombok.Getter;

@Getter
public class EventMessage extends AbstractMessage {
    private final String eventType;

    EventMessage(String content, String sender, String eventType) {
        super(content, sender);
        this.eventType = eventType;
    }

    @Override
    public MessageType getType() {
        return MessageType.EVENT;
    }
}

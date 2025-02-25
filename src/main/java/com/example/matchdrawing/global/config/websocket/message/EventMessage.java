package com.example.matchdrawing.global.config.websocket.message;

public class EventMessage extends AbstractMessage {
    private String eventType;

    EventMessage(String content, String sender, String eventType) {
        super(content, sender);
        this.eventType = eventType;
    }

    @Override
    public MessageType getType() {
        return MessageType.EVENT;
    }
}

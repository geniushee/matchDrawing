package com.example.matchdrawing.global.config.websocket.message;

public class TextMessage extends AbstractMessage {

    public TextMessage(String content, String sender){
        super(content, sender);
    }

    @Override
    public MessageType getType() {
        return MessageType.TEXT;
    }
}

package com.example.matchdrawing.global.config.websocket.message;

public class MethodMessage extends AbstractMessage {

    public MethodMessage(String content, String sender){
        super(content, sender);
    }

    @Override
    public MessageType getType() {
        return MessageType.METHOD;
    }
}

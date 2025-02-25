package com.example.matchdrawing.global.config.websocket.message;

public interface Message {
    MessageType getType();
    String getContent();
    String getSender();
    void setSender(String newSender);
    void setContent(String newContent);
}

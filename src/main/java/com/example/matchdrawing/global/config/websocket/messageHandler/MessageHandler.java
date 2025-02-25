package com.example.matchdrawing.global.config.websocket.messageHandler;

import com.example.matchdrawing.global.config.websocket.message.Message;

public interface MessageHandler {
    void handle(String destination, Message message);
}

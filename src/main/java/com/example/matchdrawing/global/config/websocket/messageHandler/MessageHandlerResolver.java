package com.example.matchdrawing.global.config.websocket.messageHandler;

import com.example.matchdrawing.global.config.websocket.message.MessageType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageHandlerResolver {
    private final Map<MessageType, MessageHandler> handlers;

    public MessageHandlerResolver(List<MessageHandler> handlers){
        this.handlers = new EnumMap<>(MessageType.class);
        handlers.forEach(handler -> {
            String className = handler.getClass().getSimpleName();
            MessageType type = MessageType.valueOf(className.replace("MessageHandler", "").toUpperCase());
            this.handlers.put(type, handler);
        });
    }

    public MessageHandler getHandler(MessageType type){
        return this.handlers.get(type);
    }
}

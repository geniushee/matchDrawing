package com.example.matchdrawing.global.config.websocket.message;

import lombok.Getter;

/**
 * message 기능 구분
 * msg를 얻는다.
 * sender(유저)를 얻는다.
 * 성격(msg인지 그림인지)을 얻는다.
 *
 * 어떤 msg 담을 것인가?
 * 채팅
 * 그림 데이터
 * 그림 기능
 * 정답 처리
 * 초대
 *
 * msg는 어떤 성격인가?
 * 단순 전달 - 채팅, 그림 데이터
 * 웹에서 동시 처리 - 그림 기능
 * 서버에서 처리 - 정답처리, 초대
 *
 * 포함되는 정보는?
 * msg, 보내는 사람, 성격
 */
@Getter
public abstract class AbstractMessage implements Message {
    private String content;
    private String sender;

    AbstractMessage(String content, String sender){
        this.content = content;
        this.sender = sender;
    }

    public abstract MessageType getType();

    public void setContent(String newContent){
        this.content = newContent;
    }

    public void setSender(String newSender){
        this.sender = newSender;
    }
}

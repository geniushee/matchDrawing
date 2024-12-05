package com.example.matchdrawing.global.config.websocket.dto;

import com.example.matchdrawing.domain.member.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.security.Principal;

/**
 * 웹소켓에서 Principal을 주입하고 받기 위한 클래스
 */
@AllArgsConstructor
@NoArgsConstructor
public class CustomPrincipal implements Principal {
    private Member member;

    @Override
    public String getName() {
        return member.getUsername();
    }
}

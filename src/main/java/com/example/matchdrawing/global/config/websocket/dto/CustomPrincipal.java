package com.example.matchdrawing.global.config.websocket.dto;

import com.example.matchdrawing.domain.member.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.security.Principal;

@AllArgsConstructor
@NoArgsConstructor
public class CustomPrincipal implements Principal {
    private Member member;

    @Override
    public String getName() {
        return member.getUsername();
    }
}

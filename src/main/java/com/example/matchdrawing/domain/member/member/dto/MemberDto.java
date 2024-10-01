package com.example.matchdrawing.domain.member.member.dto;

import com.example.matchdrawing.domain.member.member.entity.Member;
import lombok.Data;

@Data
public class MemberDto {
    private String username;

    public MemberDto(Member member){
        username = member.getUsername();
    }
}

package com.example.matchdrawing.global;

import com.example.matchdrawing.domain.member.member.entity.Member;
import com.example.matchdrawing.domain.member.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.Optional;

/**
 * RequestScope에서 사용자 정보를 캐싱하기 위한 클래스
 */
@RequestScope
@Component
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    @Lazy
    private final MemberService memberService;
    private String username;

    public boolean isLogin(){
        if(getUsername() != null){
            return true;
        }


        return false;
    }
    public String getUsername(){
        if(username != null){
            return username;
        }

        if(request.getCookies() != null) {
            Optional<Cookie> loginCookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("login")).findFirst();
            if (loginCookie.isPresent()) {
                username = loginCookie.get().getValue();
                return username;
            }
        }

        return null;
    }

    public Member getMember(){
        if(username != null) {
            return memberService.findByUsername(username).get();
        }else{
            return memberService.findByUsername(getUsername()).get();
        }
    }
}

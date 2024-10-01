package com.example.matchdrawing.global;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.Optional;

@RequestScope
@Component
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
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

        Optional<Cookie> loginCookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("login")).findFirst();
        if(loginCookie.isPresent()){
            username = loginCookie.get().getValue();
            return username;
        }

        return null;
    }
}

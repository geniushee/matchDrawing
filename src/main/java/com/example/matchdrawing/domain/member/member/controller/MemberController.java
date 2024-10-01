package com.example.matchdrawing.domain.member.member.controller;

import com.example.matchdrawing.domain.member.member.dto.MemberSignInDto;
import com.example.matchdrawing.domain.member.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String showSignUp(){
        return "member/signUp";
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute MemberSignInDto dto){
        memberService.register(dto.getName(), dto.getPassword());
        return "redirect:/roby";
    }

    @GetMapping("/signin")
    public String showSignIn(){
        return "member/signIn";
    }

    @PostMapping("/signin")
    public String signIn(@ModelAttribute MemberSignInDto dto,
                         HttpServletResponse response){
        if(memberService.check(dto.getName(), dto.getPassword())){
            Cookie cookie = new Cookie("login", dto.getName());
            cookie.setPath("/");
            cookie.setSecure(true);
            cookie.setHttpOnly(true);

            response.addCookie(cookie);
            return "home";
//            return "redirect:/roby/list";
        }

        return "redirect:/roby";
    }
}

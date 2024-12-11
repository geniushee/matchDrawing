package com.example.matchdrawing.domain.member.member.service;

import com.example.matchdrawing.domain.member.member.entity.Member;
import com.example.matchdrawing.domain.member.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member register(String name, String password) {
        Member member = Member.builder()
                .username(name)
                .password(password)
                .build();
        return memberRepository.save(member);
    }

    public boolean check(String name, String password) {
        Member member = findByUsername(name)
                .orElseThrow(() -> new IllegalArgumentException("아이디를 찾을 수 없습니다."));

        if(!member.getPassword().equals(password)){
            throw new IllegalArgumentException("비밀번호가 틀립니다.");
        }
        return true;
    }

    public Optional<Member> findByUsername(String name) {
        return memberRepository.findByUsername(name);
    }

    public Long countMembers() {
        return memberRepository.count();
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie loginCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("login"))
                .findFirst().orElse(new Cookie("login", null));


        loginCookie.setMaxAge(0);
        loginCookie.setPath("/");
        loginCookie.setValue(null);
        response.addCookie(loginCookie);
    }
}

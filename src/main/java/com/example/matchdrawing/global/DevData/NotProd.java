package com.example.matchdrawing.global.DevData;


import com.example.matchdrawing.domain.game.game.service.DrawingService;
import com.example.matchdrawing.domain.member.member.entity.Member;
import com.example.matchdrawing.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod")
@RequiredArgsConstructor
public class NotProd {

    private final DrawingService drawingService;
    private final MemberService memberService;

    @Bean
    public ApplicationRunner run(){
        return args -> {
            if(memberService.countMembers() < 3){
                Member member1 = memberService.register("user1", "1234");
                Member member2 = memberService.register("user2", "1234");
                Member member3 = memberService.register("user3", "1234");
            }
        };
    }
}

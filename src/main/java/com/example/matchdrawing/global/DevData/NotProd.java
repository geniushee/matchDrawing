package com.example.matchdrawing.global.DevData;


import com.example.matchdrawing.domain.game.game.service.AnswerService;
import com.example.matchdrawing.domain.game.game.service.DrawingService;
import com.example.matchdrawing.domain.member.member.entity.Member;
import com.example.matchdrawing.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
@Profile("!prod")
@RequiredArgsConstructor
public class NotProd {

    private final DrawingService drawingService;
    private final MemberService memberService;
    private final AnswerService answerService;

    @Bean
    public ApplicationRunner run(){
        return args -> {
            if(memberService.countMembers() < 3){
                Member member1 = memberService.register("user1", "1234");
                Member member2 = memberService.register("user2", "1234");
                Member member3 = memberService.register("user3", "1234");
            }

            if(answerService.countAnswer() < 3){
                createAnswersFromTxtFile("static/txt/basic_answers_animal.txt");
            }
        };
    }

    // 개발환경에서는 다수의 정답을 사전에 입력
    public void createAnswersFromTxtFile(String filePath){
        ClassPathResource resource = new ClassPathResource(filePath);

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream()))){
            String line;
            while((line = bufferedReader.readLine()) != null){
                String[] array = line.split(",");
                for(String answer : array){
                    answerService.addNewAnswer(answer);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

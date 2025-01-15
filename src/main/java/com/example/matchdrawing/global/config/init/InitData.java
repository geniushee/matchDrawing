package com.example.matchdrawing.global.config.init;

import com.example.matchdrawing.domain.game.game.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
@RequiredArgsConstructor
public class InitData {

    private final AnswerService answerService;

    @Bean
    public ApplicationRunner initAnswersOfAnimals(){
        return args -> {
            // 다수의 정답을 사전에 입력
            if(answerService.getTotalAnswersCount() < 144){
                createAnswersFromTxtFile("static/txt/basic_answers_animal.txt");
            }
        };
    }


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

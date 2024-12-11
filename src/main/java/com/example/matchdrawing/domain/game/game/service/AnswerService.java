package com.example.matchdrawing.domain.game.game.service;

import com.example.matchdrawing.domain.game.game.dto.CountType;
import com.example.matchdrawing.domain.game.game.entity.Answer;
import com.example.matchdrawing.domain.game.game.repository.AnswerRepository;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;

    private Long totalAnswersCount = null;

    private void setTotalAnswersCount(){
        totalAnswersCount = answerRepository.count();
    }
    public Long getTotalAnswersCount(){
        if(totalAnswersCount != null){
            return totalAnswersCount;
        }else {
            setTotalAnswersCount();
        }
        return totalAnswersCount;
    }

    @Transactional
    public void addNewAnswer(String answer){
        Answer newAnswer = new Answer(answer);
        answerRepository.save(newAnswer);
        totalAnswersCount = null;
    }

    public List<Answer> createAnswerList(int count) {
        Long id;
        List<Answer> list = new ArrayList<>();
        for(int i = 0; i < count; i++){
            id = Double.valueOf(Math.floor(Math.random() * totalAnswersCount)).longValue() + 1L;
            Answer item = findById(id);
            if(!list.contains(item))list.add(item); // 랜덤을 해도 같은게 나올 수 있다...
        }
        return list;
    }

    @Transactional
    public void countUpAnswersCount(@NotNull Long answerId, CountType type) {
        Answer answer = findById(answerId);
        switch (type) {
            case QUESTION -> answer.countUpNumberOfQuestionsAsked();
            case CORRECT -> answer.countUpCorrectAnswerNumber();
        }
        answerRepository.save(answer);
    }

    private Answer findById(Long id) {
        return answerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("찾는 정답이 없습니다."));
    }
}

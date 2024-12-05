package com.example.matchdrawing.domain.game.game.service;

import com.example.matchdrawing.domain.game.game.dto.CountType;
import com.example.matchdrawing.domain.game.game.entity.Answer;
import com.example.matchdrawing.domain.game.game.repository.AnswerRepository;
import com.example.matchdrawing.global.api.service.RestAPIService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final RestAPIService restAPIService;

    @Transactional
    public void addNewAnswer(String answer) throws JsonProcessingException {
        Answer newAnswer = new Answer(answer);
        //생각보다 단어가 제한적이여서 국어사전을 사용할 수는 없을 것 같다.
//        List<KoreanDictItem> list = restAPIService.searchWord(answer);
//        if (list.isEmpty()) {
//            throw new IllegalArgumentException("표준국어사전에 포함된 적절한 단어가 아닙니다.");
//        } else if (list.stream().filter(item -> item.getPos().equals("명사")).count() < 1) {
//            throw new IllegalArgumentException("표준국어사전에 포함된 명사가 아닙니다.");
//        }
        answerRepository.save(newAnswer);
    }

    public List<Answer> createAnswerList(int count) {
        Long totalCount = countAnswer();
        Long id;
        List<Answer> list = new ArrayList<>();
        for(int i = 0; i < count; i++){
            id = Double.valueOf(Math.floor(Math.random() * totalCount)).longValue() + 1L;
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

    public Long countAnswer() {
        return answerRepository.count();
    }
}

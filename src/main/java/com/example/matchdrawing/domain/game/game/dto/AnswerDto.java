package com.example.matchdrawing.domain.game.game.dto;

import com.example.matchdrawing.domain.game.game.entity.Answer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDto {
    private Long answerId;
    private String answer;

    public AnswerDto(Answer answer){
        this.answerId = answer.getId();
        this.answer = answer.getAnswer();
    }
}

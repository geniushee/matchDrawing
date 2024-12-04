package com.example.matchdrawing.domain.game.game.entity;

import com.example.matchdrawing.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Answer extends BaseEntity {
    @Column(unique = true)
    private String answer;
    private Integer correctAnswerNumber;
    private Integer numberOfQuestionsAsked;

    public Answer(String answer){
        this.answer = answer;
        this.correctAnswerNumber = 0;
        this.numberOfQuestionsAsked = 0;
    }

    public Double getCorrectAnswerRate(){
        return Math.round((correctAnswerNumber.doubleValue() / numberOfQuestionsAsked.doubleValue() * 100)) / 100.0;
    }

    public void countUpCorrectAnswerNumber(){
        correctAnswerNumber++;
    }

    public void countUpNumberOfQuestionsAsked(){
        numberOfQuestionsAsked++;
    }
}

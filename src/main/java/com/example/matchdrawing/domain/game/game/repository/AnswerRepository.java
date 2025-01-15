package com.example.matchdrawing.domain.game.game.repository;

import com.example.matchdrawing.domain.game.game.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByAnswer(String answer);
}

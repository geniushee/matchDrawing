package com.example.matchdrawing.domain.game.game.repository;

import com.example.matchdrawing.domain.game.game.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}

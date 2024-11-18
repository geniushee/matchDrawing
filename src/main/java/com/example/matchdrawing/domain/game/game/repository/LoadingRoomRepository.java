package com.example.matchdrawing.domain.game.game.repository;

import com.example.matchdrawing.domain.game.game.entity.DrawingRoom;
import com.example.matchdrawing.domain.game.game.entity.LoadingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadingRoomRepository extends JpaRepository<LoadingRoom, Long> {
    LoadingRoom findByRoom(DrawingRoom room);
}

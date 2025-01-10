package com.example.matchdrawing.domain.game.game.repository;

import com.example.matchdrawing.domain.game.game.entity.DrawingRoom;
import com.example.matchdrawing.domain.game.game.entity.LoadingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoadingRoomRepository extends JpaRepository<LoadingRoom, Long> {
    LoadingRoom findByRoom(DrawingRoom room);

    @Query("select lr from LoadingRoom as lr where lr.room.id = :roomId")
    Optional<LoadingRoom> findByRoomId(@Param("roomId")Long roomId);
}

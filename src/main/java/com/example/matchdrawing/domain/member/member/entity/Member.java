package com.example.matchdrawing.domain.member.member.entity;

import com.example.matchdrawing.domain.game.game.entity.DrawingRoom;
import com.example.matchdrawing.global.entity.TimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends TimeEntity {
    @Column(unique = true)
    private String username;
    private String password;
    @ManyToOne
    @JoinColumn(name = "drawing_room_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private DrawingRoom drawingRoom;
    @ManyToOne
    @JoinColumn(name = "loading_room_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private DrawingRoom loadingRoom;
}

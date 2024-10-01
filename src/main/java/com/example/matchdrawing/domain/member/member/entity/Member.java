package com.example.matchdrawing.domain.member.member.entity;

import com.example.matchdrawing.global.entity.TimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends TimeEntity {
    @Column(unique = true)
    private String username;
    private String password;
}

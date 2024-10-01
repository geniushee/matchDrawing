package com.example.matchdrawing.domain.entity;

import com.example.matchdrawing.domain.member.member.entity.Member;
import com.example.matchdrawing.global.entity.TimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrawingRoom extends TimeEntity {

    private String roomName;
    private int numOfParticipants;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "drawing_room_id")
    @Builder.Default
    private List<Member> curMember = new ArrayList<>();

    public boolean isMax(){
        return curMember.size() >= numOfParticipants;
    }

    public void enterMember(Member member){
        curMember.add(member);
    }

    public void exitMember(Member member){
        for(Member member1 : curMember){
            if(member1.getId().equals(member.getId())){
                curMember.remove(member1);
                break;
            }
        }
    }

    public boolean containMember(Member member) {
        for(Member member1 : curMember){
            if(member1.getId().equals(member.getId())){
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return curMember.isEmpty();
    }
}

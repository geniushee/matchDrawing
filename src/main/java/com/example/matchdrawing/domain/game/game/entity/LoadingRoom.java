package com.example.matchdrawing.domain.game.game.entity;

import com.example.matchdrawing.domain.member.member.entity.Member;
import com.example.matchdrawing.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoadingRoom extends BaseEntity implements Room{
    @OneToOne
    private DrawingRoom room;
    @OneToMany
    @JoinColumn(name = "loading_room_id")
    @Builder.Default
    private List<Member> completeMember = new ArrayList<>();
    private RoomStatus type;

    public void addMember(Member member){
        completeMember.add(member);
    }

    public boolean loadingComplete(){
        if(room.getCurMember().size() == completeMember.size()){
            for(Member member : room.getCurMember()){
                if(!completeMember.contains(member)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public RoomStatus getType() {
        return type;
    }

    @Override
    public void changeStatus(String status) {
        switch (status){
            case "WAITING" -> type = RoomStatus.WAITING;
            case "LOADING" -> type = RoomStatus.LOADING;
            case "PLAYING" -> type = RoomStatus.PLAYING;
        }
    }
}

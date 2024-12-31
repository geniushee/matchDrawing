package com.example.matchdrawing.domain.game.service;

import com.example.matchdrawing.domain.game.game.dto.DrawingRoomDto;
import com.example.matchdrawing.domain.game.game.dto.LoadingRoomDto;
import com.example.matchdrawing.domain.game.game.entity.RoomStatus;
import com.example.matchdrawing.domain.game.game.service.DrawingService;
import com.example.matchdrawing.domain.member.member.entity.Member;
import com.example.matchdrawing.domain.member.member.service.MemberService;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 트랜잭션이 제대로 동작하기 위해 컴포넌트로 등록
 * 아이디어 참고 사이트 : https://junhyunny.github.io/spring-boot/jpa/junit/jpa-optimistic-lock/
 */
@Component
class AsyncTransaction {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void run(Runnable runnable) {
        runnable.run();
    }
}

/**
 * 24.12.31 테스트 결론: OneToMany 관계에서 member에 LoadingRoom 데이터(id)가 저장됨으로
 * 동시성 제어 문제가 발생하지 않는다.
 */
@Slf4j
@SpringBootTest
public class DrawingServiceTest {
    @Autowired
    private DrawingService drawingService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private AsyncTransaction asyncTransaction;

    Member user1;
    Member user2;
    Member user3;

    @BeforeEach
    public void setUser() {
        user1 = memberService.findByUsername("user1").get();
        user2 = memberService.findByUsername("user2").get();
        user3 = memberService.findByUsername("user3").get();
    }

    @Test
    public void createAndEnterGameWith3users() {
        //방 개설
        DrawingRoomDto roomDto1 = drawingService.createRoom(
                user1.getUsername(),
                "roomName",
                3,
                3);

        // 방 참여
        drawingService.enterWaitingRoom(roomDto1.getId(), user2);
        drawingService.enterWaitingRoom(roomDto1.getId(), user3);

        //게임 시작
        drawingService.changeRoomStatus(roomDto1.getId(), "LOADING");
        drawingService.createLoadingRoom(roomDto1.getId());

        //게임룸 동시 입장
        CompletableFuture<Void> startUser1 = CompletableFuture.runAsync(() -> asyncTransaction.run(() -> {
            drawingService.enterGame(roomDto1.getId(), user1.getUsername());
        }));
        CompletableFuture<Void> startUser2 = CompletableFuture.runAsync(() -> asyncTransaction.run(() -> {
            drawingService.enterGame(roomDto1.getId(), user2.getUsername());
        }));
        CompletableFuture<Void> startUser3 = CompletableFuture.runAsync(() -> asyncTransaction.run(() -> {
            drawingService.enterGame(roomDto1.getId(), user3.getUsername());
        }));

        final boolean[] isLoading = new boolean[1];
        final LoadingRoomDto[] loadingRooms = new LoadingRoomDto[1];

        CompletableFuture<Void> allOf = CompletableFuture
                .allOf(startUser1, startUser2, startUser3)
                .thenRun(() -> {
                    isLoading[0] = drawingService.checkLoadingByRoomId(roomDto1.getId());
                    loadingRooms[0] = drawingService.findLoadingRoomDtoByRoomId(roomDto1.getId());
                });

        // 비동기 작업이 끝나기를 기다림.
        allOf.join();

        DrawingRoomDto roomDto2 = drawingService.findRoomDtoById(roomDto1.getId());
        System.out.printf("""
                        로딩이 완료 됐는가? isLoading : %b
                        로딩방 정보
                        로딩방 완료 멤버 :
                        %s
                        로딩방 타입은 어떤가? Type : %s
                        """,
                isLoading[0],
                String.join(", ", loadingRooms[0].getUsernames()),
                loadingRooms[0].getType());

        assertThat(isLoading[0]).as("로딩이 완료됐는지 확인").isTrue();
        assertThat(loadingRooms[0].getUsernames().size())
                .as("로딩이 완료된 인원수와 현재 참석자 수가 일치하는지 확인")
                .isEqualTo(roomDto2.getCurMember().size());
        //todo Loading을 Playing으로 변경 필요.
        assertThat(loadingRooms[0].getType()).as("").isEqualTo(RoomStatus.LOADING);
    }

    /**
     * 동시성 제어 문제가 발생하지 않아 무쓸모 테스트
     */
    @Test
    public void createAndEnterGameWith100users() {
        // 100개의 스레드를 처리할 수 있는 ExecutorService 생성, ForkJoinPool.commonPool()은 7개 밖에 생성이 안됨.
        ExecutorService customExecutor = Executors.newFixedThreadPool(100);

        //방 개설
        DrawingRoomDto roomDto1 = drawingService.createRoom(
                user1.getUsername(),
                "roomName",
                100,
                3);

        List<Member> users = new ArrayList<>();
        users.add(user1);
        users.add(user3);
        users.add(user2);

        for (int i = 4; i <= 100; i++) {
            String username = "user" + i;
            Member member = memberService.register(username, "1234");
            users.add(member);
        }

        // 방 참여
        for (Member member : users) {
            drawingService.enterWaitingRoom(roomDto1.getId(), member);
        }

        //게임 시작
        drawingService.changeRoomStatus(roomDto1.getId(), "LOADING");
        drawingService.createLoadingRoom(roomDto1.getId());

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        //게임룸 동시 입장
        for (Member member : users) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> asyncTransaction.run(() -> {
                drawingService.enterGame(roomDto1.getId(), member.getUsername());
            }), customExecutor);
            futures.add(future);
        }
        final boolean[] isLoading = new boolean[1];
        final LoadingRoomDto[] loadingRooms = new LoadingRoomDto[1];

        CompletableFuture<Void> allOf = CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    isLoading[0] = drawingService.checkLoadingByRoomId(roomDto1.getId());
                    loadingRooms[0] = drawingService.findLoadingRoomDtoByRoomId(roomDto1.getId());
                });

        // 비동기 작업이 끝나기를 기다림.
        allOf.join();

        //Executor 종료
        customExecutor.shutdown();

        DrawingRoomDto roomDto2 = drawingService.findRoomDtoById(roomDto1.getId());
        System.out.printf("""
                        로딩이 완료 됐는가? isLoading : %b
                        로딩방 정보
                        로딩방 완료 멤버 :
                        %s
                        완료 멤버 수 : %d
                        로딩방 타입은 어떤가? Type : %s
                        """,
                isLoading[0],
                String.join(", ", loadingRooms[0].getUsernames()),
                loadingRooms[0].getUsernames().size(),
                loadingRooms[0].getType());


        assertThat(isLoading[0]).as("로딩이 완료됐는지 확인").isTrue();
        assertThat(loadingRooms[0].getUsernames().size())
                .as("로딩이 완료된 인원수와 현재 참석자 수가 일치하는지 확인")
                .isEqualTo(roomDto2.getCurMember().size());
        assertThat(loadingRooms[0].getType()).as("").isEqualTo(RoomStatus.LOADING);
    }
}

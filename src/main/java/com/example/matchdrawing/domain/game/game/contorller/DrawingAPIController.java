package com.example.matchdrawing.domain.game.game.contorller;

import com.example.matchdrawing.domain.game.game.dto.ChangeRoomDto;
import com.example.matchdrawing.domain.game.game.dto.CountType;
import com.example.matchdrawing.domain.game.game.dto.DrawingRoomDto;
import com.example.matchdrawing.domain.game.game.dto.LoadingRoomRequestDto;
import com.example.matchdrawing.domain.game.game.service.AnswerService;
import com.example.matchdrawing.domain.game.game.service.DrawingService;
import com.example.matchdrawing.global.request.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class DrawingAPIController {
    private final DrawingService drawingService;
    private final AnswerService answerService;
    private final Rq rq;

    @GetMapping("/list")
    public ResponseEntity<?> showRoomList() {
        //page 설정
        int pageSize = 5;
        int pageNum = 0;
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(Sort.Order.desc("createTime"));
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(orders));

        Page<DrawingRoomDto> list = drawingService.getRoomList(pageable);
        if (list.isEmpty()) {
            return null;
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/roomInfo/{id}")
    public ResponseEntity<?> getRoomInfo(@PathVariable(name = "id")Long id){
        DrawingRoomDto dto = drawingService.findRoomDtoById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/changeStatus")
    public ResponseEntity<?> changeRoomStatus(@RequestBody ChangeRoomDto dto) {
        try {
            drawingService.changeRoomStatus(dto.getId(), dto.getStatus());

            // 로딩룸을 만드는 기능은 게임룸을 만들때 같이 만드는 방법으로 변경, 기능 분리
//            if (dto.getStatus().equals("LOADING")) {
//                drawingService.createLoadingRoom(dto.getId());
//            }

            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/isLoading")
    public ResponseEntity<?> isLoading(@RequestParam(name = "roomId") Long roomId) {
        boolean check = drawingService.checkLoadingByRoomId(roomId);
        if (check) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok().body(false);
        }
    }

    @PostMapping("/enterGame")
    public ResponseEntity<?> enterGame(@RequestBody LoadingRoomRequestDto dto){
        boolean check = drawingService.enterGame(dto.getRoomId(), dto.getUsername());
        if(check){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    @PostMapping("/exitGame")
    public ResponseEntity<?> exitGame(@RequestBody LoadingRoomRequestDto dto){
        try {
            drawingService.exitRoom(dto.getRoomId(), rq.getMember());
            return ResponseEntity.ok(true);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(false);
        }

    }

    @PostMapping("/completeLoading")
    public void completeLoading(@RequestBody LoadingRoomRequestDto dto){
        drawingService.deleteLoadingRoom(dto.getRoomId());
    }


    public record CountUpAnswerCounts(Long answerId, CountType type){}

    @PostMapping("/countUp")
    public void countUpAnswersCounts(@RequestBody CountUpAnswerCounts dto){
        answerService.countUpAnswersCount(dto.answerId, dto.type);
    }

    //todo 정답을 맞출 경우 해당 이미지를 정답에 저장하고 돌려보기 시스템 구현
}

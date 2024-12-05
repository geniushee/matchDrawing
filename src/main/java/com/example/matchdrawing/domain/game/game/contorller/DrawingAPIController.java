package com.example.matchdrawing.domain.game.game.contorller;

import com.example.matchdrawing.domain.game.game.dto.*;
import com.example.matchdrawing.domain.game.game.service.AnswerService;
import com.example.matchdrawing.domain.game.game.service.DrawingService;
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

    @PostMapping("/changeStatus")
    public ResponseEntity<?> changeRoomStatus(@RequestBody ChangeRoomDto dto) {
        try {
            drawingService.changeRoomStatus(dto.getId(), dto.getStatus());

            if (dto.getStatus().equals("LOADING")) {
                drawingService.createLoadingRoom(dto.getId());
            }

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
    public ResponseEntity<?> enterGame(@RequestBody LoadingRoomDto dto){
        boolean check = drawingService.enterGame(dto.getRoomId(), dto.getUsername());
        if(check){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    @PostMapping("/completeLoading")
    public void completeLoading(@RequestBody LoadingRoomDto dto){
        drawingService.deleteLoadingRoom(dto.getRoomId());
    }


    public record CountUpAnswerCounts(Long answerId, CountType type){}

    @PostMapping("/countUp")
    public void countUpAnswersCounts(@RequestBody CountUpAnswerCounts dto){
        answerService.countUpAnswersCount(dto.answerId, dto.type);
    }

    // 방장만 API호출로 데이터를 받아서 전체에게 공유가 되지 않음. 각자 받을경우 Random때문에 답이 각각 다름.
//    @GetMapping("/answerSet")
//    public ResponseEntity<?> getAnswerSet(@RequestParam(name = "count", defaultValue = "5")int count){
//        List<AnswerDto> list = answerService.createAnswerList(count);
//        return ResponseEntity.ok(list);
//    }

    //todo 정답을 맞출 경우 해당 이미지를 정답에 저장하고 돌려보기 시스템 구현
}

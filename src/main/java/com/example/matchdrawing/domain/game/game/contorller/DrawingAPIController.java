package com.example.matchdrawing.domain.game.game.contorller;

import com.example.matchdrawing.domain.game.game.dto.ChangeRoomDto;
import com.example.matchdrawing.domain.game.game.dto.DrawingRoomDto;
import com.example.matchdrawing.domain.game.game.dto.LoadingRoomDto;
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
            return ResponseEntity.badRequest().body(false);
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
}

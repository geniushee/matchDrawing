package com.example.matchdrawing.domain.game.controller;

import com.example.matchdrawing.domain.game.game.contorller.DrawingAPIController;
import com.example.matchdrawing.domain.game.game.dto.*;
import com.example.matchdrawing.domain.game.game.entity.RoomStatus;
import com.example.matchdrawing.domain.game.game.service.AnswerService;
import com.example.matchdrawing.domain.game.game.service.DrawingService;
import com.example.matchdrawing.domain.member.member.dto.MemberDto;
import com.example.matchdrawing.global.request.Rq;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DrawingAPIController.class)
public class DrawingAPIControllerTest {
    @MockBean
    private DrawingService drawingService;

    @MockBean
    private AnswerService answerService;

    @MockBean
    private Rq rq;

    private MockMvc mockMvc;

    private MockCookie cookie;

    private String mvcResult;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new DrawingAPIController(drawingService, answerService, rq)).build();
        cookie = new MockCookie("login", "user1");
        mvcResult = null;
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @AfterEach
    void showResult() throws UnsupportedEncodingException {
        if (mvcResult != null) {
            System.out.println(mvcResult);
        }
    }

    public DrawingRoomDto createDRDto(Long id, String name, String username) {
        MemberDto memDto = new MemberDto(username);
        return new DrawingRoomDto(id, RoomStatus.WAITING,
                LocalDateTime.now().minus(Duration.of(1, ChronoUnit.DAYS)),
                LocalDateTime.now().plus(Duration.of(1, ChronoUnit.DAYS)),
                name,
                memDto,
                new ArrayList<>(List.of(memDto)),
                3,
                1,
                new ArrayList<>(List.of(new AnswerDto(1L, "정답")))
        );
    }

    @Test
    @DisplayName("roomList 반환 테스트")
    public void showRoomList() throws Exception {
        int pageSize = 5;
        int pageNum = 0;
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(Sort.Order.desc("createTime"));
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(orders));
        List<DrawingRoomDto> list = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            list.add(createDRDto((long) i, "roomNameIs" + i, "user" + i));
        }
        when(drawingService.getRoomList(pageable)).thenReturn(new CustomPageDto<>(new PageImpl<>(list, pageable, list.size())));

        MvcResult mvcResult = mockMvc.perform(get("/api/game/list"))
                .andExpect(status().isOk())
                .andReturn();

        // json을 Object로 파싱하면 keyvalue쌍으로 되어 있어 hashmap으로 들어옴
        // objectmapper가 어떤 클래스로 맵핑하는지 알기 어려움. 외부에 전달할때는 json이므로 값을 찾기만 하면 되나, objectmapper 맘대로 key,value 클래스를 정해버림.
        CustomPageDto customPageDto1 = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CustomPageDto.class);

        assertThat(customPageDto1.getTotalPages()).as("토탈페이지 일치 확인, 토탈페이지: " + customPageDto1.getTotalPages()).isEqualTo(1);
        assertThat(customPageDto1.getPageSize()).as("페이지 사이즈 일치 확인, 페이지 사이즈: " + customPageDto1.getPageSize()).isEqualTo(pageSize);
        assertThat(customPageDto1.getNumber()).as("현재 페이지 확인, 페이지:" + customPageDto1.getNumber()).isEqualTo(0);
        assertThat(customPageDto1.getContent().get(0)).isInstanceOf(HashMap.class);
        Map roomDto = (HashMap) customPageDto1.getContent().get(0);
        assertThat(String.valueOf(roomDto.get("id"))).as("id가 일치하는지 확인").isEqualTo(list.get(0).getId().toString());
    }

    @Test
    @DisplayName("room 정보 반환 테스트")
    public void getRoomInfo() throws Exception {
        Long id = 1l;
        DrawingRoomDto dto = createDRDto(id,"roomName", "user1");

        when(drawingService.findRoomDtoById(id)).thenReturn(dto);

        MvcResult result = mockMvc.perform(get("/api/game/roomInfo/"+id))
                .andExpect(status().isOk())
                .andReturn();

        DrawingRoomDto resultDto = objectMapper.readValue(result.getResponse().getContentAsString(),DrawingRoomDto.class);


        assertThat(resultDto).as("Mapping이 됐는지 인스턴스 확인: " + resultDto.getClass().getSimpleName()).isInstanceOf(DrawingRoomDto.class);
        assertThat(resultDto.getId()).as("아이디가 일치하는지 확인: "+id).isEqualTo(dto.getId());
    }

    public void changeRoomStatus(@RequestBody ChangeRoomDto dto) {
    }

    public void isLoading(@RequestParam(name = "roomId") Long roomId) {
    }

    public void enterGame(@RequestBody LoadingRoomRequestDto dto) {
    }

    public void exitGame(@RequestBody LoadingRoomRequestDto dto) {
    }

    public void completeLoading(@RequestBody LoadingRoomRequestDto dto) {
    }

    public void countUpAnswersCounts(@RequestBody DrawingAPIController.CountUpAnswerCounts dto) {
    }
}

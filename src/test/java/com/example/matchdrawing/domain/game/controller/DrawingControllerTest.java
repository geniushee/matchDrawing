package com.example.matchdrawing.domain.game.controller;

import com.example.matchdrawing.domain.game.game.contorller.DrawingController;
import com.example.matchdrawing.domain.game.game.dto.DrawingRoomDto;
import com.example.matchdrawing.domain.game.game.entity.RoomStatus;
import com.example.matchdrawing.domain.game.game.service.DrawingService;
import com.example.matchdrawing.domain.member.member.dto.MemberDto;
import com.example.matchdrawing.global.Rq;
import com.example.matchdrawing.global.config.websocket.dto.SimpleMessageDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * DrawingController 테스트
 * WebMvcTest는 컨트롤러와 같은 것들을 테스트하기 위해 사용되는 어노테이션이다.
 * 이 테스트에서는 service부분을 Mock하여(service의 결과를 만들어 사전에 만들어서 임의로 제공) Controller만 테스트한다.
 * JpaAuditing을 사용하고 있어 어플리케이션을 실행할 때, 자동으로 Jpa를 로드하려고 한다. 하지만 Controller만 테스트하기 위한 목적으로 이번 테스트에서는 필요 없는 항목이다.
 * 따라서, JpaMetamodelMappingContext를 MockBean으로 만들어 로드한다.
 * 그러나, 이렇게 할 경우 이번 테스트만 적용되기 때문에 WebMvcTest는 Configuration을 로드하지 않는 것을 이용하여 별도의 Configuration을 설정하여 거기에 JpaAuditing을 적용한다.
 */
@WebMvcTest(controllers = DrawingController.class)
public class DrawingControllerTest {
    @MockBean
    private DrawingService drawingService;

    @MockBean
    private Rq rq;

    private MockMvc mockMvc;

    private MockCookie cookie;

    private String mvcResult;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new DrawingController(drawingService, rq)).build();
        cookie = new MockCookie("login", "user1");
        mvcResult = null;
    }

    @AfterEach
    void showResult() throws UnsupportedEncodingException {
        if (mvcResult != null) {
            System.out.println(mvcResult);
        }
    }

    private DrawingRoomDto createRoomDto(String roomName, int p) {
        return new DrawingRoomDto(
                1L,
                RoomStatus.WAITING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                roomName,
                new MemberDto("user1"),
                new ArrayList<>(),
                p,
                0,
                new ArrayList<>());
    }

    private SimpleMessageDto createMSGDto(String msg, String sender) {
        SimpleMessageDto dto = new SimpleMessageDto();
        dto.setMsg(msg);
        dto.setSender(sender);
        return dto;
    }

    @Test
    void showRoomListTest() throws Exception {
        Page<DrawingRoomDto> page = new PageImpl<>(
                List.of(createRoomDto("roomName",2)));
        when(this.drawingService.getRoomList(PageRequest.of(0, 5, Sort.by("createTime").descending()))).thenReturn(page);

        mockMvc.perform(get("/roby/list"))
                .andExpect(status().isOk())
                /* veiwResolver 순환참조 문제발생
                url의 list와 view 'list'가 순환참조되는 문제로 인하여 view이름을 roomList로 변경
                 */
                .andExpect(view().name("roomList"));
    }

    @Test
    void showCreateRoomPage() throws Exception {
        when(rq.isLogin()).thenReturn(true);

        mockMvc.perform(get("/roby/create")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(view().name("createRoom"));
    }

    @Test
    @DisplayName("Post method_createRoom")
    void createRoom() throws Exception {
        String roomName = "room1";
        int p = 3;
        int answers = 5;

        DrawingRoomDto roomDto = createRoomDto(roomName, p);

        when(rq.getUsername())
                .thenReturn("user1");
        when(drawingService.createRoom(rq.getUsername(), "room1", 3, answers))
                .thenReturn(roomDto);

        mockMvc.perform(post("/roby/create")
                        .param("roomName", roomName)
                        .param("numOfParticipants", String.valueOf(p))
                        .param("countOfAnswers", String.valueOf(answers)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/roby/room/" + roomDto.getId()));
    }

    @Test
    void enterRoom() throws Exception {
        Long roomId = 1L;
        String roomName = "room1";
        int p = 3;

        DrawingRoomDto roomDto = createRoomDto(roomName, p);

        when(rq.isLogin()).thenReturn(true);
        when(drawingService.findRoomDtoById(roomId)).thenReturn(roomDto);

        MvcResult result = mockMvc.perform(get("/roby/room/{roomId}", roomId)
                                .cookie(cookie))
                        .andExpect(status().isOk())
                        .andExpect(view().name("room"))
                        .andExpect(model().attribute("roomDto", roomDto))
                        .andReturn();
        mvcResult = result.getResponse().getContentAsString();
        /*
        webMvcTest에서는 렌더링이 되지 않기 때문에 html을 직접 확인하기는 어렵다고 한다.
        assertThat(mvcResult).as("roomDto 전달 여부 확인").contains("방이름 : " + roomDto.getRoomName());
         */
    }

}

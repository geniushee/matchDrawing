package com.example.matchdrawing.domain.game;

import com.example.matchdrawing.domain.game.game.dto.MessageDto;
import com.example.matchdrawing.domain.game.game.service.DrawingService;
import com.example.matchdrawing.global.config.websocket.message.MessageFactory;
import com.example.matchdrawing.global.config.websocket.message.MessageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Websocket테스트는 ws에 대한 configuration이 필요하여 WebMvcTest로 단위테스트를 진행하지는 못한다.
 * 따라서, SpringBootTest로 전체 Configuration을 다 가져와서 테스트를 진행해야한다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebSocketTest {
    /**
     * @LocalServerPort 를 이용하여 테스트 시 포트 정보를 가져옴
     */
    @LocalServerPort
    private int port;
    private WebSocketStompClient stompClient;
    private BlockingQueue<MessageDto> blockingQueue;

    private String WEBSOCKET_URI_MSG;
    private final String WEBSOCKET_BROKER = "/topic";
    @Autowired
    private DrawingService drawingService;
//    @MockBean
//    private WebsocketEventListener eventListener;


    @BeforeEach
    void setUp() {
        // 컨버터 생성
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = converter.getObjectMapper().registerModule(new JavaTimeModule());
        blockingQueue = new LinkedBlockingQueue<>();
        stompClient = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(converter);
        WEBSOCKET_URI_MSG = "ws://localhost:" + port + "/ws";
    }

//    @AfterEach
//    void exit(){
//        ArgumentCaptor<SessionDisconnectEvent> captor = ArgumentCaptor.forClass(SessionDisconnectEvent.class);
//        Mockito.verify(eventListener).handleWebsocketDisconnectListener(captor.capture());
//    }


    @Test
    @DisplayName("웹소켓 기능 확인")
    void webSocketTest() throws Exception {
        Long roomId = 1L;
        String username = "user1";
        // websocket에 쿠키를 넣기 위해 httpheader 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "login=" + username);

        StompSession session = stompClient
                .connectAsync(WEBSOCKET_URI_MSG + "?roomId=" + roomId, new WebSocketHttpHeaders(headers), new StompSessionHandlerAdapter() {
                })
                // 웹소켓 연결시도, 최대 1초, 연결이 안되면 TimeoutException 발생
                .get(1, TimeUnit.SECONDS);
        session.subscribe(WEBSOCKET_BROKER + "/room" + roomId, new DefaultStompFrameHandler());

        /* msg를 단순한 String으로 받지않고 객체로 받는다.
        따라서, payload를 json으로 보내기 때문에 테스트 역시 json으로 변환하여 보내서 테스트를 진행한다.
         */
        MessageDto dto = new MessageDto("TEXT", "hi", "user1", "");

        session.send("/app/room" + roomId, dto);

        MessageFactory factory = new MessageFactory();
        MessageDto message = blockingQueue.poll(3, TimeUnit.SECONDS);

        assertThat(message.getType()).as("응답의 타입 확인:" + message.getType()).isEqualTo(MessageType.TEXT);
        assertThat(message.getContent()).as("응답의 컨텐츠 확인:" + message.getContent()).isEqualTo(dto.getContent());
        assertThat(message.getSender()).as("응답의 전송자 확인:" + message.getSender()).isEqualTo(dto.getSender());

    }

    /**
     * handshake 및 interceptor를 포함하는 message brocker에서 테스트를 진행하기 위해 http 쿠키 정보를 stompclient에 전달.
     */
    @Test
    @DisplayName("스타트 이벤트 체크")
    void startEventCheck() throws Exception {
        Long roomId = 1L;
        String username = "user1";

        // websocket에 쿠키를 넣기 위해 httpheader 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "login=" + username);

        StompSession session = stompClient
                .connectAsync(WEBSOCKET_URI_MSG + "?roomId=" + roomId, new WebSocketHttpHeaders(headers), new StompSessionHandlerAdapter() {
                })
                .get(1, TimeUnit.SECONDS);
        session.subscribe(WEBSOCKET_BROKER + "/room" + roomId, new DefaultStompFrameHandler());

        MessageDto dto = new MessageDto("EVENT", null, username, "startGame");

        // 클라이언트에서 보내는 메세지
        session.send("/app/room" + roomId, dto);

        MessageDto receivedMessage = blockingQueue.poll(3, TimeUnit.SECONDS);
        assertThat(receivedMessage).as("제대로 수신했는지 확인").isNotNull();
        assertThat(receivedMessage).satisfies(msg -> {
            assertThat(msg.getContent()).matches("^/roby/game/\\d+$").as("컨텐츠가 url로 변환이 됐는지 확인");
            assertThat(msg.getSender()).isEqualTo("start").as("sender가 변환이 됐는지 확인");
        });
        System.out.println("끊기 전");
        session.disconnect();
        System.out.println("끊기 후");

//        ArgumentCaptor<SessionDisconnectEvent> captor = ArgumentCaptor.forClass(SessionDisconnectEvent.class);
//        Mockito.verify(eventListener).handleWebsocketDisconnectListener(captor.capture());
    }


    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders headers) {
//            return byte[].class;
            return MessageDto.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            blockingQueue.offer((MessageDto) payload);
        }
    }

}

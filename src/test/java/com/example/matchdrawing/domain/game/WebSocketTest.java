package com.example.matchdrawing.domain.game;

import com.example.matchdrawing.global.dto.SimpleMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
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
public class WebSocketTest {
    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private BlockingQueue<String> blockingQueue;


    private String WEBSOCKET_URI_MSG;
    private String QUERY;
    private final String WEBSOCKET_BROKER = "/topic";
    private String WEBSOCKET_URI_DRW;

    @BeforeEach
    void setUp(){
        blockingQueue = new LinkedBlockingQueue<>();
        stompClient = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        WEBSOCKET_URI_MSG = "ws://localhost:" + port + "/ws";
        WEBSOCKET_URI_DRW = "ws://localhost:"+ port + "/dr";
        QUERY = "?roomId=";
    }


    @Test
    void webSocketTest() throws Exception {
        Long roomId = 1L;
        StompSession session = stompClient
                .connectAsync(WEBSOCKET_URI_DRW, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);
        session.subscribe(WEBSOCKET_BROKER+"/drawing1", new DefaultStompFrameHandler());

        /* msg를 단순한 String으로 받지않고 객체로 받는다.
        따라서, payload를 json으로 보내기 때문에 테스트 역시 json으로 변환하여 보내서 테스트를 진행한다.
         */
        SimpleMessageDto dto = new SimpleMessageDto();
        dto.setMsg("hi");
        dto.setSender("시험");
        ObjectMapper objectMapper = new ObjectMapper();

        session.send("/app/drawing1", objectMapper.writeValueAsString(dto).getBytes());

        assertThat(blockingQueue.poll(3,TimeUnit.SECONDS)).isEqualTo(objectMapper.writeValueAsString(dto));
    }


    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            blockingQueue.offer(new String((byte[]) payload));
        }
    }

}

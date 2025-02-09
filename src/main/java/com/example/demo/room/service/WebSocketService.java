package com.example.demo.room.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.socket.WebSocketHandler;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final WebSocketClient webSocketClient;
    private final WebSocketHandler webSocketHandler;
    private WebSocketSession session;

    @PostConstruct
    public void connect() {
        try {
            session = webSocketClient.execute(
                webSocketHandler,"ws://node-app:3000"
            ).get();
        } catch (Exception e) {
            log.error("WebSocket 연결 실패: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.example.demo.room.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.room.dto.RoomCreateDto;
import com.example.demo.room.service.RoomService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody RoomCreateDto roomCreateDto) {
        // Redis 메세지 발행
        roomService.publish("test-channel", "test-message");

        String roomId = roomService.createRoom(roomCreateDto);
        return ResponseEntity.status(201).body(roomId);
    }
}

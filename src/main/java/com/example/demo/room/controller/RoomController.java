package com.example.demo.room.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.room.dto.RoomCreateJoinRequestDto;
import com.example.demo.room.dto.RoomCreateJoinResponseDto;
import com.example.demo.room.service.RoomService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody RoomCreateJoinRequestDto roomCreateDto) {
        String roomId = roomService.createRoom(roomCreateDto);
        RoomCreateJoinResponseDto roomCreateResponseDto = new RoomCreateJoinResponseDto();
        roomCreateResponseDto.setRoomId(roomId);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomCreateResponseDto);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestBody RoomCreateJoinRequestDto roomJoinDto) {
        String roomId = roomService.joinRoom(roomJoinDto);
        if (roomId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        }
        //TODO: 해당 유저가 이미 참가한 방인지 확인
        RoomCreateJoinResponseDto roomJoinResponseDto = new RoomCreateJoinResponseDto();
        roomJoinResponseDto.setRoomId(roomId);
        return ResponseEntity.status(HttpStatus.OK).body(roomJoinResponseDto);
    }
}

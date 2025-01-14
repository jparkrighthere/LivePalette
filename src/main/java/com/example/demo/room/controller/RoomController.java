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
    public ResponseEntity<?> joinRoom(@RequestBody RoomCreateJoinRequestDto roomCreateDto) {
        String roomId = roomService.joinRoom(roomCreateDto);
        RoomCreateJoinResponseDto roomCreateResponseDto = new RoomCreateJoinResponseDto();
        roomCreateResponseDto.setRoomId(roomId);
        return ResponseEntity.status(HttpStatus.OK).body(roomCreateResponseDto);
    }


}

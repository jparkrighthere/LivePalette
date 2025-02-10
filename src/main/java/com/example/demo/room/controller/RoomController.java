package com.example.demo.room.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.room.dto.RoomJoinRequestDto;
import com.example.demo.room.dto.RoomJoinResponseDto;
import com.example.demo.room.service.RoomService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestBody RoomJoinRequestDto roomJoinDto) {
        String roomId = roomService.joinRoom(roomJoinDto);
        // enterCode가 틀린 경우
        if (roomId.equals("Invalid enter code")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid enter code");
        }

        // 이미 참가한 방인 경우
        if (roomId.equals("Already joined")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Already joined");
        }
        
        RoomJoinResponseDto roomJoinResponseDto = new RoomJoinResponseDto();
        roomJoinResponseDto.setRoomId(roomId);
        return ResponseEntity.status(HttpStatus.OK).body(roomJoinResponseDto);
    }

    @DeleteMapping("{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable String roomId) {
        if (roomService.deleteRoom(roomId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Room deleted successfully");
    }
}

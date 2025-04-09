package com.example.demo.room.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.room.dto.RoomCreateJoinResponseDto;
import com.example.demo.room.dto.RoomCreateRequestDto;
import com.example.demo.room.dto.RoomJoinRequestDto;
import com.example.demo.room.service.RoomService;
import com.example.demo.user.model.CustomUserDetail;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody RoomCreateRequestDto roomCreateRequestDto) {
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String hostEmail = customUserDetail.getUsername();
        String roomId = roomService.createRoom(roomCreateRequestDto, hostEmail);
        RoomCreateJoinResponseDto roomCreateResponseDto = new RoomCreateJoinResponseDto();
        roomCreateResponseDto.setRoomId(roomId);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomCreateResponseDto);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestBody RoomJoinRequestDto roomJoinRequestDto) {
        try {
            String roomId = roomService.joinRoom(roomJoinRequestDto);
            RoomCreateJoinResponseDto roomJoinResponseDto = new RoomCreateJoinResponseDto();
            roomJoinResponseDto.setRoomId(roomId);
            return ResponseEntity.status(HttpStatus.OK).body(roomJoinResponseDto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error");
        }
    }

    // @PostMapping("/leave")
    // public ResponseEntity<?> leaveRoom(@RequestBody RoomJoinRequestDto roomJoinRequestDto) {
    //     try {
    //         String roomId = roomService.leaveRoom(roomJoinRequestDto);
    //         RoomCreateJoinResponseDto roomLeaveResponseDto = new RoomCreateJoinResponseDto();
    //         roomLeaveResponseDto.setRoomId(roomId);
    //         return ResponseEntity.status(HttpStatus.OK).body(roomLeaveResponseDto);
    //     } catch (IllegalStateException e) {
    //         return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error");
    //     }
    // }

    @DeleteMapping("{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable String roomId) {
        if (roomService.deleteRoom(roomId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Room deleted successfully");
    }
}

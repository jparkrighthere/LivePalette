package com.example.demo.room.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.room.dto.RoomCreateJoinRequestDto;
import com.example.demo.room.model.Room;
import com.example.demo.room.repository.RoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final WebSocketService webSocketService;

    public String joinRoom(RoomCreateJoinRequestDto roomJoinDto) {
        String salt = generateSalt();
        String roomId = generateHash(roomJoinDto.getUserName() + roomJoinDto.getEnterCode() + salt);

        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            room = new Room();
            room.setRoomId(roomId);
            room.setEnterCode(roomJoinDto.getEnterCode());
            room.setUserNameList(new ArrayList<String>());
        }
        String message = roomJoinDto.getUserName() + " Joined room " + roomId;
        webSocketService.sendMessage(message); // WebSocket으로 메시지 전송
        room.getUserNameList().add(roomJoinDto.getUserName());
        roomRepository.save(room);

        return roomId;
    }

    public String deleteRoom(String roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            return null;
        }
        roomRepository.deleteById(roomId);
        return roomId;
    }

    private String generateSalt() {
        return UUID.randomUUID().toString();
    }

    private String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } 
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

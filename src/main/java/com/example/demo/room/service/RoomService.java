package com.example.demo.room.service;

import com.example.demo.room.dto.RoomCreateRequestDto;
import com.example.demo.room.dto.RoomJoinRequestDto;
import com.example.demo.room.model.Room;
import com.example.demo.room.model.Room.RoomStatus;
import com.example.demo.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Value("${room.hashcode}")
    private String roomHashCode;

    public String createRoom(RoomCreateRequestDto roomCreateDto, String hostEmail) {
        String roomId = generateRoomId(roomCreateDto.getEnterCode());

        if (roomRepository.existsById(roomId)) {
            throw new IllegalStateException("Room already exists");
        }

        Room room = Room.builder()
                .roomId(roomId)
                .host(hostEmail)
                .enterCode(roomCreateDto.getEnterCode())
                .roomName(roomCreateDto.getRoomName())
                .userNameList(new HashSet<>())
                .date(roomCreateDto.getDate())
                .time(roomCreateDto.getTime())
                .description(roomCreateDto.getDescription())
                .status(RoomStatus.ONGOING)
                .build();

        roomRepository.save(room);
        return roomId;
    }

    public String joinRoom(RoomJoinRequestDto roomJoinRequestDto) {
        String roomId = generateRoomId(roomJoinRequestDto.getEnterCode());

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalStateException("Room not found"));

        if (room.getStatus() == RoomStatus.COMPLETED) {
            throw new IllegalStateException("Room is already completed");
        }

        Set<String> users = Optional.ofNullable(room.getUserNameList())
                .orElse(new HashSet<>());

        users.add(roomJoinRequestDto.getUserName());
        room.setUserNameList(users);

        roomRepository.save(room);
        return roomId;
    }

    public void leaveRoom(String roomId, String userName) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalStateException("Room not found"));

        Set<String> users = Optional.ofNullable(room.getUserNameList())
                .orElse(new HashSet<>());

        if (!users.contains(userName)) return;

        if (userName.equals(room.getHost())) {
            users.clear();
            room.setStatus(RoomStatus.COMPLETED);
        } else {
            users.remove(userName);
        }

        room.setUserNameList(users);
        roomRepository.save(room);
    }

    public List<Room> getMyRooms(String userName) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getStatus() == RoomStatus.ONGOING)
                .filter(room -> Optional.ofNullable(room.getUserNameList())
                        .map(users -> users.contains(userName))
                        .orElse(false))
                .collect(Collectors.toList());
    }

    public String deleteRoom(String roomId) {
        if (!roomRepository.existsById(roomId)) {
            return null;
        }
        roomRepository.deleteById(roomId);
        return roomId;
    }

    private String generateRoomId(String enterCode) {
        return generateHash(enterCode + roomHashCode);
    }

    private String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }
}

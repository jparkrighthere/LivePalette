package com.example.demo.room.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, String> redisPublishTemplate;
    private final String SALT_KEY_PREFIX = "salt:";
    private final String ROOM_KEY_PREFIX = "room:";

    public String createRoom(RoomCreateJoinRequestDto roomCreateDto) {
        String salt = generateSalt();
        String saltKey = SALT_KEY_PREFIX + roomCreateDto.getUserName();
        redisPublishTemplate.opsForValue().set(saltKey, salt, 1, TimeUnit.DAYS);
        String roomId = generateHash(roomCreateDto.getUserName() + roomCreateDto.getEnterCode() + salt);

        // Redis 메세지 발행
        publish(ROOM_KEY_PREFIX + roomId, "Host " + roomCreateDto.getUserName() + " created room " + roomId);
        Room room = new Room();
        room.setRoomId(roomId);
        room.setEnterCode(roomCreateDto.getEnterCode());
        room.setUserNameList(new ArrayList<String>());
        room.getUserNameList().add(roomCreateDto.getUserName());
        roomRepository.save(room);

        return roomId;
    }

    public String joinRoom(RoomCreateJoinRequestDto roomJoinDto) {
        String saltKey = SALT_KEY_PREFIX + roomJoinDto.getUserName();
        String salt = redisPublishTemplate.opsForValue().get(saltKey);
        String roomId = generateHash(roomJoinDto.getUserName() + roomJoinDto.getEnterCode() + salt);
        // Redis 메세지 발행
        publish(ROOM_KEY_PREFIX + roomId, "User " + roomJoinDto.getUserName() + " joined room " + roomId);

        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            return null;
        }
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

    private void publish(String channel, String message) {
        redisPublishTemplate.convertAndSend(channel, message);
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

    private String generateSalt() {
        return UUID.randomUUID().toString();
    }
}

package com.example.demo.room.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.room.dto.RoomCreateDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {
    private final RedisTemplate<String, String> redisStringTemplate;
    private final String SALT_KEY_PREFIX = "salt:";

    public void publish(String channel, String message) {
        redisStringTemplate.convertAndSend(channel, message);
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
            throw new RuntimeException("Error generating hash", e);
        }
    }

    private String generateSalt() {
        return UUID.randomUUID().toString();
    }

    public String createRoom(RoomCreateDto roomCreateDto) {
        String salt = generateSalt();
        String saltKey = SALT_KEY_PREFIX + roomCreateDto.getUserName();
        redisStringTemplate.opsForValue().set(saltKey, salt, 1, TimeUnit.DAYS);
        String roomId = generateHash(roomCreateDto.getUserName() + roomCreateDto.getEnterCode() + salt);

        return roomId;
    }
}

package com.example.demo.room.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class RoomCreateRequestDto {
    private String roomName;
    private String enterCode;
    private LocalDate date;
    private LocalTime time;
    private String description;
}

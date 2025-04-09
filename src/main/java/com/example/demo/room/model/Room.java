package com.example.demo.room.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "rooms")
public class Room {
    @Id
    private String roomId;
    private String roomName;
    private String host;
    private String enterCode;
    private Set<String> userNameList;
    private LocalDate date;
    private LocalTime time;
    private String description;
    private RoomStatus status;

    public enum RoomStatus {
        ONGOING, COMPLETED, 
    }
}

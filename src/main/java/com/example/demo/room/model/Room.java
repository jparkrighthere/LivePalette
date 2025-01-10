package com.example.demo.room.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "rooms")
public class Room {
    @Id
    private String roomId;
    private String enterCode;
}

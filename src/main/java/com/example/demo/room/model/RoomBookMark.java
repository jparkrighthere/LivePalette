package com.example.demo.room.model;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class RoomBookMark {
    private Timestamp time;
    private String note;
}

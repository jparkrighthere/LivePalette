package com.example.demo.room.model;

import com.example.demo.user.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;
import java.util.List;

@Data
@Document(collection = "rooms")
public class Room {
    @Id
    private String roomId;
    private Date createdAt;
    private String roomTitle;
    private String hostEmail;
    private String enterCode;
    private String[] imageUrlList;
    private String videoUrl;
    private List<RoomBookMark> bookmarks;
}

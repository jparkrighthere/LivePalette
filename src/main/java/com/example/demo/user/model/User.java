package com.example.demo.user.model;

import com.example.demo.user.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String email;
    private String password;
    private String name;
    private String profileImg;
    private String[] roomIdList;
    private Long career;
    private String field;
    private Role role;
}

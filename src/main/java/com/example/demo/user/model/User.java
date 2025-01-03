package com.example.demo.user.model;

import com.example.demo.user.Role;
import lombok.Data;

@Data
public class User {
    private String email;
    private String password;
    private String name;

    private String profileImg;
    private String[] roomIds;
    private String career;

    private Role role;
}

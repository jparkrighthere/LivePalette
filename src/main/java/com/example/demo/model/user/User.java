package com.example.demo.model.user;

import com.example.demo.user.Role;
import lombok.Data;

@Data
public class User {
    private String email;
    private String password;
    private String name;
    private Role role;
}

package com.example.demo.auth.dto;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String email;
    private String password;
}

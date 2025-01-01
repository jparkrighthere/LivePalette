package com.example.demo.auth.model;

import lombok.Data;

@Data
public class RefreshToken {
    private String token;
    private String email;
    private String expiration;
}

package com.example.demo.auth.dto;

import com.example.demo.user.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class LoginResponse {
    private final String username;
    private final String email;
    private final String field;
    private final Long career;

    public LoginResponse(User user) {
        this.username = user.getName();
        this.email = user.getEmail();
        this.field = user.getField();
        this.career = user.getCareer();
    }
}

package com.example.demo.auth.dto;

import com.example.demo.user.Role;
import com.example.demo.user.model.User;
import lombok.Getter;

@Getter
public class LoginResponse {
    private final String username;
    private final String email;
    private final String career;
    private final Role userType;

    public LoginResponse(User user) {
        this.username = user.getUserName();
        this.email = user.getEmail();
        this.career = user.getCareer();
        this.userType = user.getUserType();
    }
}

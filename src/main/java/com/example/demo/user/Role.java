package com.example.demo.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    NOT_REGISTERED("ROLE_NOT_REGISTERED"),
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String value;
}

package com.example.demo.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    CLIENT("ROLE_CLIENT"),
    DESIGNER("ROLE_DESIGNER"),
    ADMIN("ROLE_ADMIN");

    private final String value;
}

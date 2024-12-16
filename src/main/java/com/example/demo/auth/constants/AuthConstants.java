package com.example.demo.auth.constants;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class AuthConstants {
    private AuthConstants() {
        //인스턴스화 방지 (상수 클래스)
        throw new UnsupportedOperationException("이 클래스는 constants class 이므로 인스턴스화 할 수 없습니다.");
    }
    //허용된 URI목록
    public static final String[] PERMITTED_URI = {
            "/api/auth/login",
            "/api/auth/register",
    };
    //허용된 role
    public static final String[] PERMITTED_ROLES = {
            "USER",
            "ADMIN",
    };

    public static final String JWT_ISSUE_HEADER = "Set-Cookie";
    public static final String JWT_RESOLVE_HEADER = "Cookie";
    public static final String ACCESS_PREFIX = "access";
    public static final String REFRESH_PREFIX = "refresh";

    public static final List<String> PERMITTED_URI_LIST = List.of(PERMITTED_URI);
}

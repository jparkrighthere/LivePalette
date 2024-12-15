package com.example.demo.service.auth;

import com.example.demo.jwt.JwtGenerator;
import com.example.demo.jwt.JwtUtils;
import com.example.demo.service.user.CustomUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;

@Service
@Transactional(readOnly = true)
@Slf4j
@PropertySource("classpath:application-jwt.properties")
public class JwtService {
    private final CustomUserDetailService customUserDetailService;
    private final JwtGenerator jwtGenerator;
    private final JwtUtils jwtUtils;

    @Value("${jwt.access-secret}")
    private Key ACCESS_SECRET_KEY;
    @Value("${jwt.refresh-secret}")
    private Key REFRESH_SECRET_KEY;
    @Value("${jwt.access-secret}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;
    @Value("${jwt.refresh-expiration}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    public JwtService(CustomUserDetailService customUserDetailService, JwtGenerator jwtGenerator,
                      JwtUtils jwtUtils){
        this.customUserDetailService = customUserDetailService;
        this.jwtGenerator = jwtGenerator;
        this.jwtUtils = jwtUtils;
    }
}

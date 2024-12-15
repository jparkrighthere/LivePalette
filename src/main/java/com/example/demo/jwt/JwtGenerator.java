package com.example.demo.jwt;

import com.example.demo.model.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtGenerator {
    public String generateAccessToken(final Key ACCESS_SECRET, final long ACCESS_TOKEN_EXPIRATION_TIME, User user) {
        return Jwts.builder()
                .setHeader(createHeader())
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(ACCESS_SECRET,SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateRefreshToken(final Key REFRESH_SECRET, final long REFRESH_TOKEN_EXPIRATION_TIME, User user) {
        return Jwts.builder()
                .setHeader(createHeader())
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(REFRESH_SECRET,SignatureAlgorithm.HS256)
                .compact();
    }

    /*private Map<String,Object> createClaims(User user) {
        Map<String,Object> claims = new HashMap<>();
    }*/

    private Map<String,Object> createHeader() {
        Map<String,Object> header = new HashMap<>();
        header.put("typ","JWT");
        header.put("alg","HS256");
        return header;
    }
}

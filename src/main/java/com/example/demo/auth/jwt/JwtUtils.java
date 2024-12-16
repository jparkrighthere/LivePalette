package com.example.demo.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JwtUtils {
    public TokenStatus getTokenStatus(String token, Key secretKey) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return TokenStatus.AUTHENTICATED;
        } catch (ExpiredJwtException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return TokenStatus.EXPIRED;
        }catch (JwtException e) {
            log.error(e.getMessage());
            throw new JwtException(e.getMessage());
        }
    }
    public String resolveTokenFromCookie(Cookie[] cookies, String tokenPrefix) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(tokenPrefix))
                .findFirst()
                .map(Cookie::getValue)
                .orElse("");
    }
    public Key getSigningKey(String secretKey) {
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        return Keys.hmacShaKeyFor(encodedKey.getBytes(StandardCharsets.UTF_8));
    }
    public Cookie resetCookie(String tokenPrefix) {
        Cookie cookie = new Cookie(tokenPrefix, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}

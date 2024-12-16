package com.example.demo.auth.service;

import com.example.demo.auth.constants.AuthConstants;
import com.example.demo.auth.exception.TokenException;
import com.example.demo.auth.jwt.JwtGenerator;
import com.example.demo.auth.jwt.JwtUtils;
import com.example.demo.auth.jwt.TokenStatus;
import com.example.demo.user.model.User;
import com.example.demo.user.service.CustomUserDetailService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    public String generateAccessToken(HttpServletResponse response, User user){
        String accessToken = jwtGenerator.generateAccessToken(ACCESS_SECRET_KEY,ACCESS_TOKEN_EXPIRATION_TIME,user);
        ResponseCookie cookie = setTokenToCookies(AuthConstants.ACCESS_PREFIX,accessToken,ACCESS_TOKEN_EXPIRATION_TIME/1000);
        response.addHeader(AuthConstants.JWT_ISSUE_HEADER, cookie.toString());

        return accessToken;
    }
    public String generateRefreshToken(HttpServletResponse response, User user){
        String refreshToken = jwtGenerator.generateAccessToken(REFRESH_SECRET_KEY,REFRESH_TOKEN_EXPIRATION_TIME,user);
        ResponseCookie cookie = setTokenToCookies(AuthConstants.REFRESH_PREFIX,refreshToken,REFRESH_TOKEN_EXPIRATION_TIME/1000);
        response.addHeader(AuthConstants.JWT_ISSUE_HEADER, cookie.toString());

        return refreshToken;
    }

    private ResponseCookie setTokenToCookies(String tokenPrefix, String token, long expirationSeconds) {
        return ResponseCookie.from(tokenPrefix,token)
                .path("/")
                .maxAge(expirationSeconds)
                .httpOnly(true)
                .sameSite("Lax")
                .secure(true)
                .build();
    }
    public String resolveTokenFromCookies(HttpServletRequest request, String tokenPrefix) throws TokenException {
        Cookie[] cookie = request.getCookies();
        if(cookie == null)
            throw new TokenException("TOKEN_NOT_FOUND");
        return jwtUtils.resolveTokenFromCookie(cookie,tokenPrefix);
    }
    public boolean validateAccessToken(String accessToken) {
        return jwtUtils.getTokenStatus(accessToken,ACCESS_SECRET_KEY) == TokenStatus.AUTHENTICATED;
    }
    public boolean validateRefreshToken(String refreshToken) {
        return jwtUtils.getTokenStatus(refreshToken,REFRESH_SECRET_KEY) == TokenStatus.AUTHENTICATED;
    }
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(getUserEmailByAccessToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    private String getUserEmailByAccessToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(ACCESS_SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }
    public String getUserEmailByRefreshToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(REFRESH_SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }
    public void resetTokenToCookies(HttpServletResponse response) {
        Cookie accessCookie = jwtUtils.resetCookie(AuthConstants.ACCESS_PREFIX);
        Cookie refreshCookie = jwtUtils.resetCookie(AuthConstants.REFRESH_PREFIX);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}

package com.example.demo.auth.jwt;

import com.example.demo.auth.constants.AuthConstants;
import com.example.demo.auth.exception.TokenException;
import com.example.demo.auth.service.JwtService;
import com.example.demo.user.model.User;
import com.example.demo.user.service.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(isPermittedURI(request.getRequestURI())) {
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String accessToken = jwtService.resolveTokenFromCookies(request,AuthConstants.ACCESS_PREFIX);
            if(jwtService.validateAccessToken(accessToken)){
                setAuthenticationToContext(accessToken);
                filterChain.doFilter(request, response);
                return;
            }

            String refreshToken = jwtService.resolveTokenFromCookies(request,AuthConstants.REFRESH_PREFIX);
            if(jwtService.validateRefreshToken(refreshToken)){
                User user= userService.findByEmail(jwtService.getUserEmailByRefreshToken(refreshToken));
                String reissuedAccessToken = jwtService.generateAccessToken(response,user);

                setAuthenticationToContext(reissuedAccessToken);
                filterChain.doFilter(request, response);
                return;
            }
        } catch (TokenException e) {
            log.error(e.getMessage());
        }
        jwtService.resetTokenToCookies(response);
    }
    private boolean isPermittedURI(String uri) {
        return Arrays.stream(AuthConstants.PERMITTED_URI)
                .anyMatch(permitted->{
                    String replace = permitted.replace("*","");
                    return uri.contains(replace)||replace.contains(uri);
                });
    }
    private void setAuthenticationToContext(String accessToken){
        Authentication authentication = jwtService.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

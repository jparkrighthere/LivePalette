package com.example.demo.auth.filter;

import com.example.demo.auth.constants.AuthConstants;
import com.example.demo.auth.jwt.JWTUtil;
import com.example.demo.auth.jwt.TokenStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestUri = request.getRequestURI();
        if(!requestUri.endsWith("/signout")){
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if(!requestMethod.equals("POST")){
            filterChain.doFilter(request, response);
            return;
        }
        //get refresh token
        String token = null;
        Cookie[] cookies = request.getCookies();
        System.out.println(cookies.length);
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(AuthConstants.REFRESH_PREFIX))
                token = cookie.getValue();
        }
        if(jwtUtil.validateRefreshToken(token)== TokenStatus.INVALID
            /*||jwtUtil.isExistRefreshToken(reissueToken)*/){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if(jwtUtil.validateRefreshToken(token)==TokenStatus.EXPIRED){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //logout process
        jwtUtil.deleteRefreshToken(token);
        //reset cookie
        Cookie cookie = new Cookie(AuthConstants.REFRESH_PREFIX, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

package com.example.demo.auth.exception;

public class TokenException extends Exception {
    public TokenException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

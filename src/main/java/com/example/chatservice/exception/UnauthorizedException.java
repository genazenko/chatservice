package com.example.chatservice.exception;

public class UnauthorizedException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Should authorize first, please, use 'login' event";

    public UnauthorizedException() {
        super(ERROR_MESSAGE);
    }
}

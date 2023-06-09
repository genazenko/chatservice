package com.example.chatservice.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(String sessionId) {
        super("Session with id = " + sessionId + " is not found.");
    }
}

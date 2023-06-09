package com.example.chatservice.util;

import com.example.chatservice.entity.Session;
import com.example.chatservice.exception.UnauthorizedException;
import com.example.chatservice.service.SessionService;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    private final SessionService sessionService;

    public AuthUtil(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public Session checkCurrentSession(String sessionId) {
        var sessionDetails = sessionService.getSessionDetails(sessionId);
        return sessionDetails
            .filter(Session::getIsAuthorized)
            .filter(Session::getIsActive)
            .orElseThrow(UnauthorizedException::new);
    }
}

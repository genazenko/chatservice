package com.example.chatservice.service;

import com.example.chatservice.entity.Session;
import com.example.chatservice.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void createSession(String sessionId) {
        var session = Session.builder()
            .sessionId(sessionId)
            .isActive(true)
            .isAuthorized(false)
            .build();
        sessionRepository.save(session);
    }

    public void terminateSession(String sessionId) {
        sessionRepository.terminateSession(sessionId);
    }

    public void updateSessionWithUsername(String sessionId, String username) {
        sessionRepository.updateAuth(sessionId, username);
    }

    public List<Session> getActiveSessions(String username) {
        return sessionRepository.getAllActiveByUsername(username);
    }

    public Optional<Session> getSessionDetails(String sessionId) {
        return sessionRepository.getBySessionId(sessionId);
    }
}

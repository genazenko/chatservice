package com.example.chatservice.repository;

import com.example.chatservice.entity.Session;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    Session save(Session session);

    void updateAuth(String sessionId, String username);

    void terminateSession(String sessionId);

    Optional<Session> getBySessionId(String sessionId);

    List<Session> getAllActiveByUsername(String username);
}

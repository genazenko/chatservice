package com.example.chatservice.repository.impl;

import com.example.chatservice.entity.Session;
import com.example.chatservice.exception.SessionNotFoundException;
import com.example.chatservice.repository.SessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@Slf4j
public class InMemorySessionRepository implements SessionRepository {
    private final ConcurrentMap<String, Session> sessionStore = new ConcurrentHashMap<>();

    @Override
    public Session save(Session session) {
        log.info("Creating new session with ID = {}", session.getSessionId());
        return sessionStore.putIfAbsent(session.getSessionId(), session);
    }

    @Override
    public synchronized void updateAuth(String sessionId, String username) {
        Optional.ofNullable(sessionStore.get(sessionId))
            .map(session -> {
                session.setIsAuthorized(true);
                session.setUsername(username);
                return session;
            })
            .ifPresentOrElse(session -> sessionStore.replace(sessionId, session),
                () -> {
                    throw new SessionNotFoundException(sessionId);
                });
    }

    @Override
    public synchronized void terminateSession(String sessionId) {
        Optional.ofNullable(sessionStore.get(sessionId))
            .map(session -> {
                session.setIsActive(false);
                return session;
            })
            .ifPresentOrElse(session -> sessionStore.replace(sessionId, session),
                () -> {
                    throw new SessionNotFoundException(sessionId);
                });
    }

    @Override
    public Optional<Session> getBySessionId(String sessionId) {
        return Optional.ofNullable(sessionStore.get(sessionId));
    }

    @Override
    public List<Session> getAllActiveByUsername(String username) {
        return sessionStore
            .values()
            .stream()
            .filter(Session::getIsActive)
            .filter(session -> username.equals(session.getUsername()))
            .toList();
    }
}

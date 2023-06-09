package com.example.chatservice.repository;

import com.example.chatservice.entity.Session;
import com.example.chatservice.repository.impl.InMemorySessionRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SessionRepositoryTest {

    private SessionRepository sessionRepository;

    @Test
    void save() {
        sessionRepository = new InMemorySessionRepository();
        var session = Session.builder()
            .sessionId("session")
            .isAuthorized(false)
            .isActive(true)
            .build();
        sessionRepository.save(session);

        var optSession = sessionRepository.getBySessionId(session.getSessionId());
        assertTrue(optSession.isPresent());
        assertEquals(session.getSessionId(), optSession.get().getSessionId());
        assertTrue(optSession.get().getIsActive());
        assertFalse(optSession.get().getIsAuthorized());
    }

    @Test
    void updateAuth() {
        sessionRepository = new InMemorySessionRepository();
        var session = Session.builder()
            .sessionId("session")
            .isAuthorized(false)
            .isActive(true)
            .build();
        sessionRepository.save(session);

        var optSession = sessionRepository.getBySessionId(session.getSessionId());
        assertTrue(optSession.isPresent());
        assertFalse(optSession.get().getIsAuthorized());

        sessionRepository.updateAuth(session.getSessionId(), "user");

        optSession = sessionRepository.getBySessionId(session.getSessionId());
        assertTrue(optSession.isPresent());
        assertTrue(optSession.get().getIsAuthorized());
        assertNotNull(optSession.get().getUsername());
    }

    @Test
    void terminateSession() {
        sessionRepository = new InMemorySessionRepository();
        var session = Session.builder()
            .sessionId("session")
            .isAuthorized(false)
            .isActive(true)
            .build();
        sessionRepository.save(session);

        var optSession = sessionRepository.getBySessionId(session.getSessionId());
        assertTrue(optSession.isPresent());
        assertTrue(optSession.get().getIsActive());

        sessionRepository.terminateSession(session.getSessionId());

        optSession = sessionRepository.getBySessionId(session.getSessionId());
        assertTrue(optSession.isPresent());
        assertFalse(optSession.get().getIsActive());
    }

    @Test
    void getAllSessions() {
        sessionRepository = new InMemorySessionRepository();
        var session = Session.builder()
            .sessionId("session1")
            .isAuthorized(true)
            .isActive(true)
            .username("user")
            .build();

        var otherSession = Session.builder()
            .sessionId("session2")
            .isAuthorized(true)
            .isActive(true)
            .username("user")
            .build();

        sessionRepository.save(session);
        sessionRepository.save(otherSession);

        var activeSessions = sessionRepository.getAllActiveByUsername(session.getUsername());
        assertEquals(2, activeSessions.size());
        var sessionIds = activeSessions.stream().map(Session::getSessionId).toList();

        assertTrue(sessionIds.contains(session.getSessionId()));
        assertTrue(sessionIds.contains(otherSession.getSessionId()));
    }
}

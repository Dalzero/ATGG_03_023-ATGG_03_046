package com.joeshim.server.service;

import com.joeshim.server.repo.InMemorySessionRepository;
import com.joeshim.server.thread.channel.ClientConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class AuthService {
    private final InMemorySessionRepository sessionRepository;

    public AuthService(InMemorySessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public String login(String userId, String password, ClientConnection channel) {
        if (!isValidUser(userId, password)) {
            return null;
        }

        String sessionId = UUID.randomUUID().toString();

        sessionRepository.save(sessionId, userId, channel);

        log.info(">>> User Logged in: {} (Session: {})", userId, sessionId);
        return sessionId;
    }

    public void logout(String sessionId) {
        sessionRepository.delete(sessionId);
        log.info(">>> Session closed: {}", sessionId);
    }

    private boolean isValidUser(String userId, String password) {
        if ("marco".equals(userId) && "nhnacademy123".equals(password)) return true;
        if ("alice".equals(userId) && "1234".equals(password)) return true;
        return false;
    }
}
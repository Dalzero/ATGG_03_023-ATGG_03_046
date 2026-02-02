package com.joeshim.server.repo;

import com.joeshim.server.thread.channel.ClientConnection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionRepository {
    private final Map<String, String> sessionStore = new ConcurrentHashMap<>();

    private final Map<String, ClientConnection> connectionStore = new ConcurrentHashMap<>();

    public void save(String sessionId, String userId, ClientConnection channel) {
        sessionStore.put(sessionId, userId);
        connectionStore.put(userId, channel);
    }

    public void delete(String sessionId) {
        String userId = sessionStore.remove(sessionId);
        if (userId != null) {
            connectionStore.remove(userId);
        }
    }

    public String getUserId(String sessionId) {
        return sessionStore.get(sessionId);
    }

    public ClientConnection getChannel(String userId) {
        return connectionStore.get(userId);
    }
}
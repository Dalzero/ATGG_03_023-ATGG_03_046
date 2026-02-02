package com.joeshim.server.method.parser.handler.impl;

/**
 * LOGOUT 처리
 */

import com.joeshim.common.Message;
import com.joeshim.common.type.impl.LoginType;
import com.joeshim.server.method.parser.handler.Handler;
import com.joeshim.server.method.response.ErrorCodes;
import com.joeshim.server.method.response.Responses;
import com.joeshim.server.service.AuthService;
import com.joeshim.server.thread.channel.ClientConnection;

import java.util.Map;

public class LogoutHandler implements Handler {
    private final AuthService authService;

    public LogoutHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Message handle(Message request, ClientConnection channel) {
        String sessionId = request.getHeader().getSessionId();

        if (sessionId == null || sessionId.isBlank()) {
            return Responses.error(ErrorCodes.AUTH_INVALID_SESSION, "Session ID is missing");
        }

        authService.logout(sessionId);

        return Responses.ok(
                LoginType.LOGOUT_SUCCESS.wire(),
                Map.of("message", "Logged out successfully")
        );
    }
}
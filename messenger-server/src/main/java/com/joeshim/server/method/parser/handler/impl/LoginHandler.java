package com.joeshim.server.method.parser.handler.impl;

/**
 * LOGIN 처리 (서버 Auth 로직?? 호출)
 * 응답 Message 생성해서 반환
 */

import com.joeshim.server.method.parser.handler.Handler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joeshim.common.Message;
import com.joeshim.common.dto.auth.LoginRequest;
import com.joeshim.common.dto.auth.LoginSuccessResponse;
import com.joeshim.common.type.impl.LoginType;
import com.joeshim.server.method.response.ErrorCodes;
import com.joeshim.server.method.response.Responses;
import com.joeshim.server.service.AuthService;
import com.joeshim.server.thread.channel.ClientConnection;

public class LoginHandler implements Handler {
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public LoginHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Message handle(Message request, ClientConnection channel) {
        try {
            JsonNode dataNode = request.getData();
            LoginRequest loginReq = mapper.treeToValue(dataNode, LoginRequest.class);

            String sessionId = authService.login(loginReq.getUserId(), loginReq.getPassword(), channel);

            if (sessionId != null) {
                LoginSuccessResponse data = new LoginSuccessResponse(
                        loginReq.getUserId(),
                        "Welcome!"
                );
                return Responses.ok(LoginType.LOGIN_SUCCESS.wire(), data, sessionId);
            } else {
                return Responses.error(ErrorCodes.AUTH_INVALID_CREDENTIALS, "Invalid username or password");
            }

        } catch (JsonProcessingException | IllegalArgumentException e) {
            return Responses.error(ErrorCodes.REQUEST_BAD_REQUEST, "Invalid login request format");
        }
    }
}
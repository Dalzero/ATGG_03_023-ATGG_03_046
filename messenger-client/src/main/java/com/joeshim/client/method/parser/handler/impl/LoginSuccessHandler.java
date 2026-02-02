package com.joeshim.client.method.parser.handler.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.joeshim.client.method.parser.JsonSupport;
import com.joeshim.client.method.parser.handler.ResponseHandler;
import com.joeshim.client.method.repo.ClientStateRepository;
import com.joeshim.client.method.view.Printer;
import com.joeshim.common.Message;
import com.joeshim.common.dto.auth.LoginSuccessResponse;
import com.joeshim.common.type.impl.LoginType;

import java.util.Objects;

/**
 * LOGIN-SUCCESS 수신 시 sessionId를 state에 저장
 * ==================================================
 * 책임:
 * - sessionId를 header에서 꺼내 ClientStateRepository에 저장
 * - 가능하면 userId도 응답 data에서 꺼내 저장
 * - 성공 메시지 출력
 * 주의:
 * - sessionId는 "Header"가 서버가 발급으로 보는게 안전
 * - data는 스펫 변경 가능성이 있으니, data 파싱이 실패해도 sessionId 저장은 진행하도록 방어적으로 처리
 * ========================================================================
 * 로그인 성공 처리에서 가장 중요한 것은 sessionId 저장임, 그 이후 모든 요청(logout, 방 입장, 채팅, 파일 전송)은
 * sessionId를 header에 넣는 흐름 ㅇㅇ
 * LoginSuccessResponse DTO는 있으면 파싱해서 userId/message 사용, 없으면 무시함 (서버 스펙이 변해도 클라가 죽지 않게)
 * stateRepository.update(...)로 상태 갱신을 원자적으로 처리함
 */
public class LoginSuccessHandler implements ResponseHandler {

    private final ClientStateRepository stateRepository;
    private  final Printer printer;

    public LoginSuccessHandler(ClientStateRepository stateRepository, Printer printer) {
        if (Objects.isNull(stateRepository)) {
            throw new IllegalArgumentException("stateRepository is null");
        }
        if (Objects.isNull(printer)) {
            throw new IllegalArgumentException("printer is null");
        }
        this.stateRepository = stateRepository;
        this.printer = printer;
    }

    @Override
    public boolean supports(String type) {
        return LoginType.LOGIN_SUCCESS.wire().equals(type);
    }

    @Override
    public void handle(Message message) {
        String sessionId = (Objects.isNull(message.getHeader())) ? null : message.getHeader().getSessionId();
        if (Objects.isNull(sessionId) || sessionId.isBlank()) {
            printer.error("login success received, but sessionId is missing");
            return;
        }
        String userId = null;
        String serverMessage = null;

        JsonNode data = message.getData();
        if (Objects.nonNull(data) && !data.isNull()) {
            try {
                LoginSuccessResponse body = JsonSupport.fromNode(data, LoginSuccessResponse.class);
                userId = body.getUserId();
                serverMessage = body.getMessage();
            } catch (Exception ignore) {
                // data 파싱 실패해도 sessionId 저장은 계속해야하지 않을까...? 흠
            }
        }
        final String finalUserId = userId;
        stateRepository.update(state -> state.login(finalUserId, sessionId));

        if (Objects.nonNull(serverMessage) && !serverMessage.isBlank()) {
            printer.info("%s (sessionId = %s)".formatted(serverMessage, sessionId));
        } else {
            printer.info("login success (sessionId = %s)".formatted(sessionId));
        }

    }
}

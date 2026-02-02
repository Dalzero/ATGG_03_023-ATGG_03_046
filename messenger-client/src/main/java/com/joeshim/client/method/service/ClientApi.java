package com.joeshim.client.method.service;

import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import com.joeshim.client.method.repo.OutboundMessageQueue;
import com.joeshim.common.Message;
import com.joeshim.common.dto.auth.LoginRequest;
import com.joeshim.common.type.impl.LoginType;

import java.util.Objects;

/**
 * '요청 메시지 생성 + 전송' 담당
 * login(userId, pw)
 * logout()
 * createRoom(name) 등
 * header.timestamp 채우기
 * header.sessionId (state에서 가져오기, LOGIN 제외)
 * type(wire string) 넣기
 * data 구성 (addData or DTO)
 * ================================================
 *  클라이언트가 서버로 보내는 "요청" API
 *  책임:
 *  어떤 타입의 요청을 보낼지 결정
 *  필요한 DTO 생성
 *  MessageFactory로 Message 만들기
 *  OutboundMessageQueue에 넣기 (enqueue)
 * ================================================
 * 통신(send)는 SenderLoop가 담당
 */
public class ClientApi {
    private final MessageFactory messageFactory;
    private final OutboundMessageQueue outboundQueue;

    public ClientApi(MessageFactory messageFactory, OutboundMessageQueue outboundQueue) {
        if (Objects.isNull(messageFactory)) {
            throw new IllegalArgumentException("messageFactory is null");
        }
        if (Objects.isNull(outboundQueue)) {
            throw new IllegalArgumentException("outboundQueue is null");
        }
        this.messageFactory = messageFactory;
        this.outboundQueue = outboundQueue;
    }

    public void login(String userId, String password) {
        if (Objects.isNull(userId)) {
            throw new IllegalArgumentException("userId is null");
        }
        if (userId.isBlank()) {
            throw new IllegalArgumentException("userId is blank");
        }
        if (Objects.isNull(password)) {
            throw new IllegalArgumentException("password is null");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("password is blank");
        }
        LoginRequest body = new LoginRequest(userId, password);
        Message request = messageFactory.noSession(LoginType.LOGIN.wire(), body);
        enqueue(request);
    }

    public void logout() {
        Message request = messageFactory.withSession(LoginType.LOGOUT.wire(), null);
        enqueue(request);
    }

    public void enqueue(Message message) {
        try {
            outboundQueue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("enqueue interrupted", e);
        }
    }
}

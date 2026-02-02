package com.joeshim.client.method.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.joeshim.client.method.parser.JsonSupport;
import com.joeshim.client.method.repo.ClientStateRepository;
import com.joeshim.common.Header;
import com.joeshim.common.Message;

import java.util.Objects;

/**
 * 클라이언트가 서버로 보낼 Message 생성 규칙을 모아둔 factory
 * 규칙:
 * type은 common enum의 wire() 값만 사용
 * sessionId가 필요한 요청은 ClientStateRepository에서 꺼내 Header에 넣음
 * data는 DTO -> JsonNode로 변환해서 Message에 넣음 (data가 없으면 null로 넘기고, 최종 JSON 표현 규칙은 PacketUtils가 처리
 * 장점:
 * ClientApi가 무슨 요청을 보내는지만 집중할 수 있음
 * 확장 가능성 고려
 */
public class MessageFactory {
    private final ClientStateRepository stateRepository;

    public  MessageFactory(ClientStateRepository stateRepository) {
        if (Objects.isNull(stateRepository)) {
            throw new IllegalArgumentException("stateRepository is null");
        }
        this.stateRepository = stateRepository;
    }

    private JsonNode toNodeOrNull(Object dtoOrNull) {
        if (Objects.isNull(dtoOrNull)) {
            return null;
        }
        return JsonSupport.toNode(dtoOrNull);
    }

    // sessionId가 필요없는 요청
    public Message noSession(String type, Object dtoOrNull) {
        if (Objects.isNull(type)) {
            throw new IllegalArgumentException("type is null");
        }
        if (type.isBlank()) {
            throw new IllegalArgumentException("type is blank");
        }
        Header header = new Header(type);  // timestamp, hear에서 now
        return new Message(header, toNodeOrNull(dtoOrNull));
    }

    // 나머지
    public Message withSession(String type, Object dtoOrNull) {
        if (Objects.isNull(type)) {
            throw new IllegalArgumentException("type is null");
        }
        if (type.isBlank()) {
            throw new IllegalArgumentException("type is blank");
        }
        String sessionId = stateRepository.get().getSessionId();
        if (Objects.isNull(sessionId) || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId is missing (login required");
        }
        Header header = new Header(type, sessionId);
        return new Message(header, toNodeOrNull(dtoOrNull));

    }
}

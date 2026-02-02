package com.joeshim.server.method.response;

/**
 * ok(type, data) / error(code, message) 같은 팩토리 메서드
 * Error는 항상 ErrorBody로 내려가게 고정
 */


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.joeshim.common.Header;
import com.joeshim.common.Message;
import com.joeshim.common.dto.error.ErrorBody;
import com.joeshim.common.type.impl.ErrorType;

public class Responses {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static Message ok(String type, Object data) {
        return createMessage(type, true, null, data);
    }

    public static Message ok(String type, Object data, String sessionId) {
        return createMessage(type, true, sessionId, data);
    }

    public static Message error(String code, String message) {
        ErrorBody errorBody = new ErrorBody(code, message);
        return createMessage(ErrorType.ERROR.wire(), false, null, errorBody);
    }

    private static Message createMessage(String type, boolean success, String sessionId, Object data) {
        Header header = new Header(type, null, sessionId, success);

        return new Message(header, mapper.valueToTree(data));
    }
}
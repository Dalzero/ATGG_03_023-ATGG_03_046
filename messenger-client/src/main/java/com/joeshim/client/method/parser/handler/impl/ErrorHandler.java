package com.joeshim.client.method.parser.handler.impl;

// ERROR 수신 시 ErrorBody 출력

import com.fasterxml.jackson.databind.JsonNode;
import com.joeshim.client.method.parser.JsonSupport;
import com.joeshim.client.method.parser.handler.ResponseHandler;
import com.joeshim.client.method.view.Printer;
import com.joeshim.common.Message;
import com.joeshim.common.PacketUtils;
import com.joeshim.common.dto.error.ErrorBody;
import com.joeshim.common.type.impl.ErrorType;

import java.util.Objects;

/**
 * 서버가 ERROR 타입을 보냈을 때 처리
 * - ErrorBody가(code, message)를 출력
 * - data가 없거나 파싱 실패해도 클라가 죽지 않게 방어적으로 처리
 */
public final class ErrorHandler implements ResponseHandler {

    private final Printer printer;

    public ErrorHandler(Printer printer) {
        if (Objects.isNull(printer)) {
            throw new IllegalArgumentException("printer is null");
        }
        this.printer = printer;
    }

    @Override
    public boolean supports(String type) {
        return ErrorType.ERROR.wire().equals(type);
    }

    @Override
    public void handle(Message message) {
        JsonNode data = message.getData();
        if (Objects.isNull(data) || data.isNull()) {
            printer.error("ERROR: (on body)");
            return;
        }
        try {
            ErrorBody body = JsonSupport.fromNode(data, ErrorBody.class);
            printer.error("ERROR [%s]: %s".formatted(body.getCode(), body.getMessage()));
        } catch (Exception e) {
            printer.error("ERROR (unparsed body): " + data.toString());
        }
    }
}

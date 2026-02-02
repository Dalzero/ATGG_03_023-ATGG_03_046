package com.joeshim.client.method.parser;

import com.joeshim.client.method.parser.handler.ResponseHandler;
import com.joeshim.client.method.view.Printer;
import com.joeshim.common.Message;

import java.util.List;
import java.util.Objects;

/**
 * 수신 Message.header.type 보고 처리 분기
 * Map<String, ResponseHandler>로 등록 형태 (확장성)
 * ================================================
 * ReceiverLoop는 읽기만 하고
 * 실제 처리(상태 갱신/출력/알림)는 Handler 들이 담당함
 * List 기반 설계
 * - 타입/핸들러가 계속 추가될 때 등록이 단순함
 * 초반에는 규칙을 엄격히 고정하지 않아도 됨
 */
public class ResponseDispatcher {

    private final List<ResponseHandler> handlers;
    private final Printer printer;

    public ResponseDispatcher(List<ResponseHandler> handlers, Printer printer) {
        if (Objects.isNull(handlers)) {
            throw new IllegalArgumentException("handler is null");
        }
        if (handlers.isEmpty()) {
            throw new IllegalArgumentException("handler is empty");
        }
        if (Objects.isNull(printer)) {
            throw new IllegalArgumentException("printer is null");
        }
        this.handlers = List.copyOf(handlers);
        this.printer = printer;
    }

    public void dispatch(Message message) {
        if (Objects.isNull(message) || Objects.isNull(message.getHeader())) {
            printer.warn("invalid message: header missing");
            return;
        }
        String type = message.getHeader().getType();
        if (Objects.isNull(type) || type.isBlank()) {
            printer.warn("invalid message: type missing");
            return;
        }
        for (ResponseHandler handler : handlers) {
            if (handler.supports(type)) {
                handler.handle(message);
                return;
            }
        }
        printer.warn("no handler for type= %s".formatted(type));

    }
}

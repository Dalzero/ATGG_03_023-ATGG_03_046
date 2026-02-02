package com.joeshim.client.method.parser.command;

import com.joeshim.client.method.service.ClientApi;
import com.joeshim.client.method.view.Printer;

import java.util.Objects;

/**
 * logout은 sessionId가 필요하므로, 로그인 안 된 상태면 withSession() 막힘
 * 여기선 그 예외를 사용자 메시지로 바꿔서 출력만 하고 끝
 */
public class LogoutCommand implements Command {

    private final ClientApi clientApi;
    private final Printer printer;

    public LogoutCommand(ClientApi clientApi, Printer printer) {
        if (Objects.isNull(clientApi)) {
            throw new IllegalArgumentException("clientApi is null");
        }
        if (Objects.isNull(printer)) {
            throw new IllegalArgumentException("printer is null");
        }
        this.clientApi = clientApi;
        this.printer = printer;
    }

    @Override
    public void execute() {
        try {
            clientApi.logout();
            printer.info("logout request sent");
        } catch (IllegalArgumentException e) {
            // 로그인 안 된 상태(세션 없음)에서 logout 요청하면 MessageFactory.withSession에서 터질 듯?
            printer.warn("logout failed: %s".formatted(e.getMessage()));
        }
    }
}

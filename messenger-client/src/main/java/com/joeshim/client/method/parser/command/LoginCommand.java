package com.joeshim.client.method.parser.command;

import com.joeshim.client.method.service.ClientApi;
import com.joeshim.client.method.view.Printer;

import java.util.Objects;

/**
 * /login {userId} {password}
 */
public final class LoginCommand implements Command {
    private final ClientApi clientApi;
    private final Printer printer;
    private final String userId;
    private final String password;

    public LoginCommand(ClientApi clientApi, Printer printer, String userId, String password) {
        if (Objects.isNull(clientApi)) {
            throw new IllegalArgumentException("clientApi is null");
        }
        if (Objects.isNull(printer)) {
            throw new IllegalArgumentException("printer is null");
        }
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
            throw new IllegalArgumentException("password is null");
        }
        this.clientApi = clientApi;
        this.printer = printer;
        this.userId = userId;
        this.password = password;
    }

    @Override
    public void execute() {
        clientApi.login(userId, password);
        printer.info("login request sent: %s".formatted(userId));
    }
}

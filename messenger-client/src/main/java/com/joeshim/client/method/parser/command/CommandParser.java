package com.joeshim.client.method.parser.command;

import com.joeshim.client.method.service.ClientApi;
import com.joeshim.client.method.view.Printer;

import java.util.Objects;

public final class CommandParser {

    private final ClientApi clientApi;
    private final Printer printer;
    private final Runnable onExit;

    public CommandParser(ClientApi clientApi, Printer printer, Runnable onExit) {
        if (Objects.isNull(clientApi)) throw new IllegalArgumentException("clientApi is null");
        if (Objects.isNull(printer)) throw new IllegalArgumentException("printer is null");
        if (Objects.isNull(onExit)) throw new IllegalArgumentException("onExit is null");
        this.clientApi = clientApi;
        this.printer = printer;
        this.onExit = onExit;
    }

    public Command parse(String line) {
        if (Objects.isNull(line) || line.isBlank()) {
            throw new IllegalArgumentException("empty command");
        }

        String[] tokens = line.trim().split("\\s+");
        String cmd = tokens[0];

        return switch (cmd) {
            case "/login" -> {
                if (tokens.length != 3) throw new IllegalArgumentException("usage: /login {userId} {password}");
                yield new LoginCommand(clientApi, printer, tokens[1], tokens[2]);
            }
            case "/logout" -> {
                if (tokens.length != 1) throw new IllegalArgumentException("usage: /logout");
                yield new LogoutCommand(clientApi, printer);
            }
            case "/exit" -> {
                if (tokens.length != 1) throw new IllegalArgumentException("usage: /exit");
                yield new ExitCommand(onExit, printer);
            }
            default -> throw new IllegalArgumentException("unknown command: " + cmd);
        };
    }
}
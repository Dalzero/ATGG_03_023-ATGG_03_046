package com.joeshim.client.runnable;

import com.joeshim.client.method.parser.command.CommandParser;
import com.joeshim.client.method.parser.command.Command;
import com.joeshim.client.method.view.Printer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

@Slf4j
public final class ConsoleLoop implements Runnable {

    private final Printer printer;
    private volatile boolean running = true;

    private volatile CommandParser commandParser;

    public ConsoleLoop(Printer printer) {
        if (Objects.isNull(printer)) {
            throw new IllegalArgumentException("printer is null");
        }
        this.printer = printer;
    }

    public void setCommandParser(CommandParser commandParser) {
        if (Objects.isNull(commandParser)) {
            throw new IllegalArgumentException("commandParser is null");
        }
        this.commandParser = commandParser;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        if (commandParser == null) {
            throw new IllegalStateException("commandParser is not set");
        }

        printer.info("commands: /login {id} {pw}, /logout, /exit");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (running) {
                // stdout(JSON) 오염 방지 -> stderr로 프롬프트
                System.err.print("> ");

                String line = br.readLine();
                if (line == null) {
                    printer.info("console closed");
                    break;
                }

                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    Command command = commandParser.parse(line);
                    command.execute();
                } catch (IllegalArgumentException e) {
                    printer.warn(e.getMessage());
                } catch (Exception e) {
                    printer.error("command failed: " + e.getMessage());
                    log.error("[client] command error", e);
                }
            }
        } catch (IOException e) {
            printer.error("console io error: " + e.getMessage());
            log.error("[client] console io error", e);
        } finally {
            printer.info("console stopped");
        }
    }
}
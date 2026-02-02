package com.joeshim.client.method.parser.command;

import com.joeshim.client.method.view.Printer;

import java.util.Objects;

public final class ExitCommand implements Command {

    private final Runnable onExit;
    private final Printer printer;

    public ExitCommand(Runnable onExit, Printer printer) {
        if (Objects.isNull(onExit)) throw new IllegalArgumentException("onExit is null");
        if (Objects.isNull(printer)) throw new IllegalArgumentException("printer is null");
        this.onExit = onExit;
        this.printer = printer;
    }

    @Override
    public void execute() {
        printer.info("exiting...");
        onExit.run();
    }
}
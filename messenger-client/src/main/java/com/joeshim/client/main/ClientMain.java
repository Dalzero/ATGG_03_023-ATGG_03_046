package com.joeshim.client.main;

import com.joeshim.client.method.parser.ResponseDispatcher;
import com.joeshim.client.method.parser.command.CommandParser;
import com.joeshim.client.method.parser.handler.ResponseHandler;
import com.joeshim.client.method.parser.handler.impl.ErrorHandler;
import com.joeshim.client.method.parser.handler.impl.LoginSuccessHandler;
import com.joeshim.client.method.parser.handler.impl.LogoutSuccessHandler;
import com.joeshim.client.method.repo.ClientStateRepository;
import com.joeshim.client.method.repo.InMemoryClientStateRepository;
import com.joeshim.client.method.repo.OutboundMessageQueue;
import com.joeshim.client.method.service.ClientApi;
import com.joeshim.client.method.service.MessageFactory;
import com.joeshim.client.method.view.Printer;
import com.joeshim.client.runnable.ConsoleLoop;
import com.joeshim.client.runnable.ReceiverLoop;
import com.joeshim.client.runnable.SenderLoop;
import com.joeshim.client.thread.channel.Connection;
import com.joeshim.client.thread.channel.ServerConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
public final class ClientMain {

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 12345;

    private ClientMain() {}

    public static void main(String[] args) {
        String host = parseHost(args);
        int port = parsePort(args);

        Printer printer = new Printer();

        try (Connection connection = new ServerConnection(host, port)) {
            // stdout(JSON)와 섞이면 안 되므로, Printer가 stderr로 찍도록 전제
            printer.info("connected: " + connection.remote());

            // repositories
            ClientStateRepository stateRepo = new InMemoryClientStateRepository();
            OutboundMessageQueue outboundQueue = new OutboundMessageQueue();

            // services
            MessageFactory messageFactory = new MessageFactory(stateRepo);
            ClientApi clientApi = new ClientApi(messageFactory, outboundQueue);

            // response handlers + dispatcher
            List<ResponseHandler> handlers = List.of(
                    new ErrorHandler(printer),
                    new LoginSuccessHandler(stateRepo, printer),
                    new LogoutSuccessHandler(stateRepo, printer)
            );
            ResponseDispatcher responseDispatcher = new ResponseDispatcher(handlers, printer);

            // loops (io)
            SenderLoop senderLoop = new SenderLoop(connection, outboundQueue, printer);
            ReceiverLoop receiverLoop = new ReceiverLoop(connection, responseDispatcher, printer);

            // console
            ConsoleLoop consoleLoop = new ConsoleLoop(printer);
            CommandParser commandParser = new CommandParser(clientApi, printer, consoleLoop::stop);
            consoleLoop.setCommandParser(commandParser);

            Thread senderThread = new Thread(senderLoop, "client-sender");
            Thread receiverThread = new Thread(receiverLoop, "client-receiver");
            Thread consoleThread = new Thread(consoleLoop, "client-console");

            senderThread.start();
            receiverThread.start();
            consoleThread.start();

            // 콘솔 종료(/exit)까지 대기
            consoleThread.join();

            // 종료 처리: sender/receiver가 블로킹이면 connection close로 같이 정리됨
            printer.info("closing connection...");

        } catch (IOException e) {
            printer.error("failed to connect: " + e.getMessage());
            log.error("[client] connect error", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            printer.warn("client interrupted");
        } catch (Exception e) {
            printer.error("client unexpected error: " + e.getMessage());
            log.error("[client] unexpected error", e);
        }
    }

    private static String parseHost(String[] args) {
        if (Objects.isNull(args) || args.length == 0) {
            return DEFAULT_HOST;
        }
        return (args[0] == null || args[0].isBlank()) ? DEFAULT_HOST : args[0];
    }

    private static int parsePort(String[] args) {
        if (Objects.isNull(args) || args.length < 2) {
            return DEFAULT_PORT;
        }
        try {
            int port = Integer.parseInt(args[1]);
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("port out of range: " + port);
            }
            return port;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid port: " + args[1], e);
        }
    }
}
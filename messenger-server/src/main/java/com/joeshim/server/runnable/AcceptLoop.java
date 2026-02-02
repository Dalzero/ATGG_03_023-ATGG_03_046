package com.joeshim.server.runnable;

/**
 * ServerSocket.accept() 반복
 * accept된 Socket을 ClientWorker에 넘겨 Thread 실행
 */

import com.joeshim.server.method.parser.Dispatcher;
import com.joeshim.server.thread.channel.ClientConnection;
import com.joeshim.server.thread.pool.ThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class AcceptLoop implements Runnable {
    private final ServerSocket serverSocket;
    private final Dispatcher dispatcher;
    private final ThreadFactory threadFactory;

    public AcceptLoop(ServerSocket serverSocket, Dispatcher dispatcher, ThreadFactory threadFactory) {
        this.serverSocket = serverSocket;
        this.dispatcher = dispatcher;
        this.threadFactory = threadFactory;
    }

    @Override
    public void run() {
        log.info(">>> AcceptLoop started. Waiting for connections...");

        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                log.info(">>> New connection from: {}", clientSocket.getRemoteSocketAddress());

                ClientConnection clientChannel = new ClientConnection(clientSocket);

                ConnectionWorker worker = new ConnectionWorker(clientSocket, dispatcher, clientChannel);

                threadFactory.newClientThread(worker).start();

            } catch (IOException e) {
                log.error("Accept error: {}", e.getMessage());
                break;
            }
        }
    }
}
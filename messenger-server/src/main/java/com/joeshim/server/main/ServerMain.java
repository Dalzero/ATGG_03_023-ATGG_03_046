package com.joeshim.server.main;

/**
 * 포트 결정(고정 또는 args)
 * ServerSocket 생성
 * Accept 루프 (Runnable) 시작
 * 종료 처리(예외 시 close)
 * 비즈니스 로직/파싱 로직 x
 */

import com.joeshim.server.method.parser.Dispatcher;
import com.joeshim.server.runnable.AcceptLoop;
import com.joeshim.server.thread.pool.ThreadFactory;
import com.joeshim.common.ProtocolConstants;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerMain {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(ProtocolConstants.PORT);
            System.out.println(">>> Server initialized on port " + ProtocolConstants.PORT);
            ThreadFactory threadFactory = new ThreadFactory();
            Dispatcher dispatcher = new Dispatcher();
            AcceptLoop acceptLoop = new AcceptLoop(serverSocket, dispatcher, threadFactory);

            Thread acceptThread = threadFactory.newAcceptThread(acceptLoop);
            acceptThread.start();
        } catch (IOException e) {
            System.err.println(">>> Server failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
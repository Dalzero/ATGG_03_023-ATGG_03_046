package com.joeshim.server.runnable;

/**
 * Socket 하나 처리
 * InputStream에서 PackUtils.readMessage()로 요청 수신
 * Dispatcher 호출해서 응답 Message 만들기
 * OutputStream으로 PacketUtils.formatMessage() 보내기
 * 연결 종료 처리 (예외/close)
 */

import com.joeshim.common.Message;
import com.joeshim.common.PacketUtils;
import com.joeshim.server.method.parser.Dispatcher;
import com.joeshim.server.thread.channel.ClientConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class ConnectionWorker implements Runnable {
    private final Socket socket;
    private final Dispatcher dispatcher;
    private final ClientConnection channel;

    public ConnectionWorker(Socket socket, Dispatcher dispatcher, ClientConnection channel) {
        this.socket = socket;
        this.dispatcher = dispatcher;
        this.channel = channel;
    }

    @Override
    public void run() {
        System.out.println(">>> ClientWorker started: " + socket.getRemoteSocketAddress());
        try {
            while (socket.isConnected() && !socket.isClosed()) {
                Message request = PacketUtils.readMessage(channel.getInputStream());

                if (request == null) {
                    break;
                }

                System.out.println("[RECV] " + request.getHeader().getType());

                Message response = dispatcher.dispatch(request, channel);

                if (response != null) {
                    channel.send(response);
                }
            }
        } catch (EOFException e) {
            log.info("Client disconnected normally: {}", socket.getRemoteSocketAddress());
        } catch (IOException e) {
            log.error("Connection error ({}): {}", socket.getRemoteSocketAddress(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Protocol violation: {}", e.getMessage());
        } finally {
            channel.close();
        }
    }
}

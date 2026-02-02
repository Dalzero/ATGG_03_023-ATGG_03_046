package com.joeshim.server.thread.channel;

/**
 * 내부에 Socket 또는 OutputStream 보유
 * send(Message msg) (PackUtils 사용)
 * close()
 * 이게 둬야 나중에 "브로드캐스트/귓속말 전달"할 때 서버 로직이 소켓을 직접 만지지 않게 됨 (확장성)
 */

import com.joeshim.common.Message;
import com.joeshim.common.PacketUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

@Slf4j
public class ClientConnection {
    private final Socket socket;
    private final DataOutputStream out;

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public synchronized void send(Message message) {
        try {
            byte[] packet = PacketUtils.formatMessage(message);
            out.write(packet);
            out.flush();
        } catch (IOException e) {
            log.error("Failed to send message: {}", e.getMessage());
            close();
        }
    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // close 상태인거임
        }
    }
}
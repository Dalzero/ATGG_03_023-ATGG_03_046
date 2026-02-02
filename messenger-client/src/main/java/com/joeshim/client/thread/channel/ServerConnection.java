package com.joeshim.client.thread.channel;


import com.joeshim.common.InboundPacket;
import com.joeshim.common.Message;
import com.joeshim.common.PacketUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

/**
 * Socket 기반 Connection 구현체
 * 책임:
 * Socket 생성/관리
 * common.PacketUtils를 이용해 Message <-> bytes 변환 후 송수신
 * 사용 규칙
 * - send(): SenderLoop에서만 호출
 * - read(): ReceiverLoop에서만 호출
 * ============================================================
 * 실제 TCP 소켓으로 Message를 주고받는 가장 바닥 구현체
 * 변환 규칙은 전부 messenger-common의 PacketUtils에 위임해서 서버와 100% 동일한 프레이밍 (message-length:)을 공유함
 * send()에서 flush()를 강제해서 콘솔 입력/짧은 메시지 전송 시 "버퍼에 남아 전송 지연"되는 상황에 줄임
 * rend()는 블로킹이기 때문에 ReceiverLoop 전용으로 쓰는 구조
 */
public final class ServerConnection implements Connection {

    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;

    public ServerConnection(String host, int port) throws IOException {
        if (Objects.isNull(host)) {
            throw new IllegalArgumentException("host is null");
        }
        if (host.isBlank()) {
            throw new IllegalArgumentException("host is blank");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("port out of range: %d".formatted(port));
        }
        this.socket = new Socket(host, port);
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }

    public ServerConnection(Socket socket) throws IOException {
        if (Objects.isNull(socket)) {
            throw new IllegalArgumentException("socket is null");
        }
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }

    @Override
    public boolean isOpen() {
        return !socket.isClosed();
    }

    @Override
    public String remote() {
        return String.valueOf(socket.getRemoteSocketAddress());
    }

    @Override
    public void send(Message message) throws IOException {
        if (Objects.isNull(message)) {
            throw new IllegalArgumentException("message is null");
        }
        byte[] framed = PacketUtils.formatMessage(message);
        out.write(framed);
        out.flush();
    }

    @Override
    public Message read() throws IOException {
        // PackUtils.readMessage()가 내부적으로 EOFException/IllegalArgumentException 등을 던질 수도 있음
        // ReceiveLoop에서 IOException 중심으로 종료 처리하도록 그냥 위로 던짐
        return readPacket().message();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public InboundPacket readPacket() throws IOException {
        return PacketUtils.readPacket(in);
    }
}

package com.joeshim.client.thread.channel;

import com.joeshim.common.InboundPacket;
import com.joeshim.common.Message;

import java.io.Closeable;
import java.io.IOException;

/**
 * 클라이언트 통신 계층을 추상화한 인터페이스.
 * - 지금은 Socket 기반(ServerConnection)으로 구현하고,
 * - 나중에 NIO 기반으로 바꿔도 ClientApi / SenderLoop / ReceiverLoop는 그대로 유지하기 위한 목적.
 * 사용 규칙(권장):
 * - send(): SenderLoop(송신 전용 스레드)에서만 호출
 * - read(): ReceiverLoop(수신 전용 스레드)에서만 호출
 * =========================================================================================
 * 클라이언트 상위 로직이 통신 구현(Socket/NIO)에 직접 의존하지 않게 하는 장치
 * SenderLoop는 Connection.send()만 호출하고, ReceiverLoop는 Connection.read()만 호출하게 분리하면,
 * 파일 전송/대량 메시지에서만 콘솔 입력이 멈추지 않는 구조(추가구현 포함)가 깔끔하게 나옴
 * Closeable을 구현해서 try-with-resources로 정리 가능하게 해뒀고, remote()는 로그 찍을 때 유용
 */

public interface Connection extends Closeable {

    // 연결이 열려있는지 여부
    boolean isOpen();

    // 로깅/디버깅용 원격지 식별 문자열
    String remote();

    /**
     * 메시지 1개 전송.
     * @throws IOException 네트워크/스트림 문제
     */
    void send(Message message) throws IOException;

    /**
     * 메시지 1개 수신(블로킹).
     * @throws IOException 네트워크/스트림 문제(EOF 포함 정책은 구현체/PacketUtils에 따름)
     */
    Message read() throws IOException;

    InboundPacket readPacket() throws IOException;

    /**
     * 연결 종료.
     * Closeable이므로 try-with-resources 사용 가능.
     */
    @Override
    void close() throws IOException;
}
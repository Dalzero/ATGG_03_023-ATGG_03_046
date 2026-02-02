package com.joeshim.client.runnable;

import com.joeshim.client.method.parser.ResponseDispatcher;
import com.joeshim.client.method.view.Printer;
import com.joeshim.client.thread.channel.Connection;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 무한 루프로 channel.read() 호출
 * 받은 Message를 method/parser/ResponseDispatcher에 전당
 * 예외/종료 시 close() 처리
 * =======================================================
 * 서버로부터 들어오는 모든 메시지를 지속적으로 수신하는 전용 루프
 * 요청에 대한 응답(Response)뿐 아니라, 서버가 임의로 보내는 Push(브로드캐스트 메시지 등)도 전부 여기로 들어옴
 * 따라서 ReceiverLoop는 읽어서 dispatcher로 넘기는 것만 책임지고,
 * 실제 처리 로직은 ResponseDispatcher/Handler에 맡김
 */

@Slf4j
public final class ReceiverLoop implements Runnable {

    private final Connection connection;
    private final ResponseDispatcher dispatcher;
    private final Printer printer;

    public ReceiverLoop(Connection connection, ResponseDispatcher dispatcher, Printer printer) {
        if (Objects.isNull(connection)) throw new IllegalArgumentException("connection is null");
        if (Objects.isNull(dispatcher)) throw new IllegalArgumentException("dispatcher is null");
        if (Objects.isNull(printer)) throw new IllegalArgumentException("printer is null");
        this.connection = connection;
        this.dispatcher = dispatcher;
        this.printer = printer;
    }

    @Override
    public void run() {
        log.info("[client] receiver started: {}", connection.remote());
        while (connection.isOpen()) {
            try {
                var packet = connection.readPacket();

                // payload JSON 원문 그대로 출력
                printer.jsonRaw(new String(packet.payload(), StandardCharsets.UTF_8));

                // 처리 로직 위임
                dispatcher.dispatch(packet.message());

            } catch (EOFException e) {
                printer.info("server closed connection");
                break;
            } catch (IOException e) {
                printer.error("receive failed: " + e.getMessage());
                break;
            } catch (IllegalArgumentException e) {
                printer.error("invalid message received: " + e.getMessage());
                break;
            } catch (Exception e) {
                printer.error("receiver unexpected error: " + e.getMessage());
                break;
            }
        }
        log.info("[client] receiver stopped");
    }
}
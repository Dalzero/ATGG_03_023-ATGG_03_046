package com.joeshim.client.runnable;

import com.joeshim.client.method.repo.OutboundMessageQueue;
import com.joeshim.client.method.view.Printer;
import com.joeshim.client.thread.channel.Connection;
import com.joeshim.common.Message;
import com.joeshim.common.PacketUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * OutboundMessageQueue에서 Message를 꺼내 실제 네트워크로 전송하는 전용 루프
 * 구조상 규칙:
 * - ConsoleLoop/Command는 큐에 넣기만 함
 * - 네트워크 전송은 이 SenderLoop만 담당함
 * 장점:
 * - 파일 전송/대량 전송/네트워크 지연 상황에서도 입력(UI)이 멈추지 않음
 * ============================================================
 * take()에서 블로킹되기 때문에, queue가 비어 있으면 CPU를 안 쓰고 기다림
 * IOException이 나면 연결이 끊겼을 가능성이 높으니 sender를 종료함
 * sender가 죽으면 더 이상 전송이 안되므로, 실제 제품이년 "재연결/재시작"을 넣을 수 있는데 그건 나중에
 */

@Slf4j
public class SenderLoop implements Runnable {

    private final Connection connection;
    private final OutboundMessageQueue outboundQueue;
    private final Printer printer;

    public SenderLoop(Connection connection, OutboundMessageQueue outboundQueue, Printer printer) {
        if (Objects.isNull(connection)) {
            throw new IllegalArgumentException("connection is null");
        }
        if (Objects.isNull(outboundQueue)) {
            throw new IllegalArgumentException("outboundMessage is null");
        }
        if (Objects.isNull(printer)) {
            throw new IllegalArgumentException("printer is null");
        }
        this.connection = connection;
        this.outboundQueue = outboundQueue;
        this.printer = printer;
    }

    @Override
    public void run() {
        log.info("[client] sender started: {}", connection.remote());
        while (connection.isOpen()) {
            try {
                Message message = outboundQueue.take();

                // 1) "실제 전송 payload JSON"을 그대로 콘솔에 출력
                String payloadJson = extractPayloadJson(message);
                if (payloadJson != null) {
                    printer.jsonRaw(payloadJson);
                }

                // 2) 전송
                connection.send(message);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                printer.warn("sender interrupted");
                break;
            } catch (IOException e) {
                printer.error("send failed: " + e.getMessage());
                break;
            } catch (Exception e) {
                printer.error("sender unexpected error: " + e.getMessage());
                break;
            }
        }
        log.info("[client] sender stopped");
    }

    private String extractPayloadJson(Message message) {
        try {
            byte[] framed = PacketUtils.formatMessage(message); // "message-length: N\n" + payload
            int nl = -1;
            for (int i = 0; i < framed.length; i++) {
                if (framed[i] == (byte) '\n') {
                    nl = i;
                    break;
                }
            }
            if (nl < 0 || nl + 1 >= framed.length) {
                return null;
            }
            return new String(framed, nl + 1, framed.length - (nl + 1), StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 출력 실패는 치명적이지 않게 (전송은 계속)
            log.debug("[client] failed to extract payload json: {}", e.getMessage(), e);
            return null;
        }
    }
}

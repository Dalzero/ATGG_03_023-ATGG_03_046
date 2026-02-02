package com.joeshim.client.method.repo;

// BlockingQueue 래퍼
import com.joeshim.client.exception.QueueInterruptedException;
import com.joeshim.common.Message;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 클라이언트 -> 서버로 나가는 메시지 전용 queue
 * ConsoleLoop(입력)에서 곧바로 socket.send()를 호출하면 파일 전송/대량 전송/네트워크 지연 때 입력이 멈출 수 있음
 * 그래서 'enqueue'만 하고, 실제 전송은 SenderLoop(전송 스레드)가 수행하도록 분리
 * ================================================================================
 * 이후 ClientApi는 send를 직접 하지 않고 무조건 queue.put(message)만 한다
 * 실제 네트워크 전송은 다음 파일 SenderLoop가 담당함
 */
public final class OutboundMessageQueue {

    private final BlockingQueue<Message> queue;

    public OutboundMessageQueue() {
        this.queue = new LinkedBlockingQueue<>();
    }

    public OutboundMessageQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    // 메시지 1개를 queue에 넣음(블로킹 가능)
    public void put(Message message) throws InterruptedException {
        if (Objects.isNull(message)) {
            throw new IllegalArgumentException("message is null");
        }
        queue.put(message);
    }

    // 메시지 1개를 queue에서 꺼냄(큐가 비면 블로킹)
    public Message take() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }

    public void enqueue(Message message) {
        if (Objects.isNull(message)) {
            throw new IllegalArgumentException("message is null");
        }
        try {
            put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 상태 복구 (필수)
            throw new QueueInterruptedException("enqueue interrupted", e);
        }
    }
}

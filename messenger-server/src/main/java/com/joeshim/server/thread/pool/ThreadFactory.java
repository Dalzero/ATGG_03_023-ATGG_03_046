package com.joeshim.server.thread.pool;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactory {
    private final AtomicInteger clientThreadCounter = new AtomicInteger(1);

    public Thread newAcceptThread(Runnable r) {
        Thread t = new Thread(r, "accept-thread");
        t.setDaemon(false);
        return t;
    }

    public Thread newClientThread(Runnable r) {
        Thread t = new Thread(r, "client-" + clientThreadCounter.getAndIncrement());
        t.setDaemon(true);
        return t;
    }
}
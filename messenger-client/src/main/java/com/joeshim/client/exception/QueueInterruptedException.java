package com.joeshim.client.exception;

public class QueueInterruptedException extends RuntimeException {
    public QueueInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
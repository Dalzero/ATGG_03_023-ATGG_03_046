package com.joeshim.common;

import java.util.Arrays;
import java.util.Objects;

public record InboundPacket(byte[] payload, Message message) {

    public InboundPacket {
        Objects.requireNonNull(payload, "payload is null");
        Objects.requireNonNull(message, "message is null");
        payload = Arrays.copyOf(payload, payload.length);
    }

}

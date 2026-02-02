package com.joeshim.common.type.impl;

import com.joeshim.common.type.MessageTypeKey;

public enum ChatType implements MessageTypeKey {
    CHAT_MESSAGE("CHAT-MESSAGE"),
    CHAT_MESSAGE_SUCCESS("CHAT-MESSAGE-SUCCESS"),
    PRIVATE_MESSAGE("PRIVATE-MESSAGE"),
    PRIVATE_MESSAGE_SUCCESS("PRIVATE-MESSAGE-SUCCESS");

    private final String wire;

    ChatType(String wire) {
        this.wire = wire;
    }

    @Override
    public String wire() {
        return wire;
    }
}

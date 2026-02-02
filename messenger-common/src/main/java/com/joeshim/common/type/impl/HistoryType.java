package com.joeshim.common.type.impl;

import com.joeshim.common.type.MessageTypeKey;

public enum HistoryType implements MessageTypeKey {
    CHAT_MESSAGE_HISTORY("CHAT-MESSAGE-HISTORY"),
    CHAT_MESSAGE_HISTORY_SUCCESS("CHAT-MESSAGE-HISTORY-SUCCESS");

    private final String wire;

    HistoryType(String wire) {
        this.wire = wire;
    }
    @Override
    public String wire() {
        return wire;
    }
}

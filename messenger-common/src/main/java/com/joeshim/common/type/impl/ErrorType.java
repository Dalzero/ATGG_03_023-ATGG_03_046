package com.joeshim.common.type.impl;

import com.joeshim.common.type.MessageTypeKey;

public enum ErrorType implements MessageTypeKey {
    ERROR("ERROR");

    private final String wire;

    ErrorType(String wire) {
        this.wire = wire;
    }

    @Override
    public String wire() {
        return wire;
    }
}


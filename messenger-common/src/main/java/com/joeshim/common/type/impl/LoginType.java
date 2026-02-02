package com.joeshim.common.type.impl;

import com.joeshim.common.type.MessageTypeKey;

public enum LoginType implements MessageTypeKey {
    LOGIN("LOGIN"),
    LOGIN_SUCCESS("LOGIN-SUCCESS"),
    LOGOUT("LOGOUT"),
    LOGOUT_SUCCESS("LOGOUT-SUCCESS");

    private final String wire;

    LoginType(String wire) {
            this.wire = wire;
    }

    @Override
    public String wire() {
        return wire;
    }
}

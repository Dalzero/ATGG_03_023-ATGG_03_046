package com.joeshim.common.type.impl;

import com.joeshim.common.type.MessageTypeKey;

public enum UserListType implements MessageTypeKey {
    USER_LIST("USER-LIST"),
    USER_LIST_SUCCESS("USER-LIST-SUCCESS");

    private final String wire;

    UserListType(String wire) {
        this.wire = wire;
    }
    @Override
    public String wire() {
        return wire;
    }
}
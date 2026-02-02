package com.joeshim.common.type.impl;

import com.joeshim.common.type.MessageTypeKey;

public enum ChatRoomType implements MessageTypeKey {
    CHAT_ROOM_CREATE("CHAT-ROOM-CREATE"),
    CHAT_ROOM_CREATE_SUCCESS("CHAT-ROOM-CREATE-SUCCESS"),
    CHAT_ROOM_LIST("CHAT-ROOM-LIST"),
    CHAT_ROOM_LIST_SUCCESS("CHAT-ROOM-LIST-SUCCESS"),
    CHAT_ROOM_ENTER("CHAT-ROOM-ENTER"),
    CHAT_ROOM_ENTER_SUCCESS("CHAT-ROOM-ENTER-SUCCESS"),
    CHAT_ROOM_EXIT("CHAT-ROOM-EXIT"),
    CHAT_ROOM_EXIT_SUCCESS("CHAT-ROOM-EXIT-SUCCESS");

    private final String wire;

    ChatRoomType(String wire) {
        this.wire = wire;
    }

    @Override
    public String wire() {
        return wire;
    }
}
package com.joeshim.common.dto.room;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

@Getter
@ToString
public class ChatRoomListSuccessResponse {
    private final List<ChatRoomInfo> rooms;

    @JsonCreator
    public ChatRoomListSuccessResponse(
            @JsonProperty("rooms") List<ChatRoomInfo> rooms
    ) {
        if (Objects.isNull(rooms)) {
            throw new IllegalArgumentException("rooms is null");
        }
        this.rooms = List.copyOf(rooms);
    }
}
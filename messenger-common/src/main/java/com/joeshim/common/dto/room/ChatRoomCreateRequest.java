package com.joeshim.common.dto.room;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class ChatRoomCreateRequest {
    private final String roomName;

    @JsonCreator
    public ChatRoomCreateRequest(
            @JsonProperty("roomName") String roomName
    ) {
        if (Objects.isNull(roomName)) {
            throw new IllegalArgumentException("roomName is null");
        }
        if (roomName.isBlank()) {
            throw new IllegalArgumentException("roomName is blank");
        }
        this.roomName = roomName;
    }
}
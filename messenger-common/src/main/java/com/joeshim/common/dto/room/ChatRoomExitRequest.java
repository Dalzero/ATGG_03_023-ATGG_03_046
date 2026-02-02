package com.joeshim.common.dto.room;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ChatRoomExitRequest {
    private final long roomId;

    @JsonCreator
    public ChatRoomExitRequest(
            @JsonProperty("roomId") long roomId
    ) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("roomId must be > 0");
        }
        this.roomId = roomId;
    }
}
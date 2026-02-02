package com.joeshim.common.dto.room;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class ChatRoomCreateSuccessResponse {
    private final long roomId;
    private final String roomName;

    @JsonCreator
    public ChatRoomCreateSuccessResponse(
            @JsonProperty("roomId") long roomId,
            @JsonProperty("roomName") String roomName
    ) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("roomId must be > 0");
        }
        if (Objects.isNull(roomName)) {
            throw new IllegalArgumentException("roomName is null");
        }
        if (roomName.isBlank()) {
            throw new IllegalArgumentException("roomName is blank");
        }
        this.roomId = roomId;
        this.roomName = roomName;
    }
}
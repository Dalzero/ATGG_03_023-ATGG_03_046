package com.joeshim.common.dto.room;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
public class ChatRoomInfo {
    private final long roomId;
    private final String roomName;
    private final int userCount;

    @JsonCreator
    public ChatRoomInfo(
            @JsonProperty("roomId") long roomId,
            @JsonProperty("roomName") String roomName,
            @JsonProperty("userCount") int userCount
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
        if (userCount < 0) {
            throw new IllegalArgumentException("userCount must be >= 0");
        }
        this.roomId = roomId;
        this.roomName = roomName;
        this.userCount = userCount;
    }
}
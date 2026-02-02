package com.joeshim.common.dto.room;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class ChatRoomExitSuccessResponse {
    private final long roomId;
    private final String message;

    @JsonCreator
    public ChatRoomExitSuccessResponse(
            @JsonProperty("roomId") long roomId,
            @JsonProperty("message") String message
    ) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("roomId must be > 0");
        }
        if (Objects.isNull(message)) {
            throw new IllegalArgumentException("message is null");
        }
        if (message.isBlank()) {
            throw new IllegalArgumentException("message is blank");
        }
        this.roomId = roomId;
        this.message = message;
    }
}
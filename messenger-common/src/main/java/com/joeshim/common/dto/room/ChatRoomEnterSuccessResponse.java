package com.joeshim.common.dto.room;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

@ToString
@Getter
public class ChatRoomEnterSuccessResponse {
    private final long roomId;
    private final List<String> users;

    @JsonCreator
    public ChatRoomEnterSuccessResponse(
            @JsonProperty("roomId") long roomId,
            @JsonProperty("users") List<String> users
    ) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("roomId must be > 0");
        }
        if (Objects.isNull(users)) {
            throw new IllegalArgumentException("users is null");
        }
        this.roomId = roomId;
        this.users = List.copyOf(users);
    }
}
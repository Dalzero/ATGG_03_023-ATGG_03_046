package com.joeshim.common.dto.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChatMessageHistoryRequest {
    private final long roomId;
    private final int limit;
    private final long beforeMessageId;

    @JsonCreator
    public ChatMessageHistoryRequest(
            @JsonProperty("roomId") long roomId,
            @JsonProperty("limit") int limit,
            @JsonProperty("beforeMessageId") long beforeMessageId
    ) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("roomId must be > 0");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be > 0");
        }
        if (beforeMessageId < 0) {
            throw new IllegalArgumentException("beforeMessageId must be >= 0");
        }
        this.roomId = roomId;
        this.limit = limit;
        this.beforeMessageId = beforeMessageId;
    }
}
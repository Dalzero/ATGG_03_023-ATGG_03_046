package com.joeshim.common.dto.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChatMessageSuccessResponse {
    private final long roomId;
    private final long messageId;

    @JsonCreator
    public ChatMessageSuccessResponse(
            @JsonProperty("roomId") long roomId,
            @JsonProperty("messageId") long messageId
    ) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("roomId must be > 0");
        }
        if (messageId <= 0) {
            throw new IllegalArgumentException("messageId must be > 0");
        }
        this.roomId = roomId;
        this.messageId = messageId;
    }
}
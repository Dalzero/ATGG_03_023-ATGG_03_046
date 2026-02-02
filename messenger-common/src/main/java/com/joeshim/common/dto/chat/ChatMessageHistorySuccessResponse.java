package com.joeshim.common.dto.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

@Getter
@ToString
public class ChatMessageHistorySuccessResponse {
    private final long roomId;
    private final List<ChatHistoryMessage> messages;
    private final boolean hasMore;

    @JsonCreator
    public ChatMessageHistorySuccessResponse(
            @JsonProperty("roomId") long roomId,
            @JsonProperty("messages") List<ChatHistoryMessage> messages,
            @JsonProperty("hasMore") boolean hasMore
    ) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("roomId must be > 0");
        }
        if (Objects.isNull(messages)) {
            throw new IllegalArgumentException("messages is null");
        }
        this.roomId = roomId;
        this.messages = List.copyOf(messages);
        this.hasMore = hasMore;
    }
}
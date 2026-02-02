package com.joeshim.common.dto.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class PrivateMessageRequest {
    private final String senderId;
    private final String receiverId;
    private final String message;

    @JsonCreator
    public PrivateMessageRequest(
            @JsonProperty("senderId") String senderId,
            @JsonProperty("receiverId") String receiverId,
            @JsonProperty("message") String message
    ) {
        if (Objects.isNull(senderId)) {
            throw new IllegalArgumentException("senderId is null");
        }
        if (senderId.isBlank()) {
            throw new IllegalArgumentException("senderId is blank");
        }
        if (Objects.isNull(receiverId)) {
            throw new IllegalArgumentException("receiverId is null");
        }
        if (receiverId.isBlank()) {
            throw new IllegalArgumentException("receiverId is blank");
        }
        if (Objects.isNull(message)) {
            throw new IllegalArgumentException("message is null");
        }
        if (message.isBlank()) {
            throw new IllegalArgumentException("message is blank");
        }
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
    }
}

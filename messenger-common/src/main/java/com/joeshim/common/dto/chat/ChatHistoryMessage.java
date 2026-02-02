package com.joeshim.common.dto.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Objects;

@Getter
@ToString
public class ChatHistoryMessage {
    private final long messageId;
    private final String senderId;
    private final String senderName;
    private final Instant timestamp;
    private final String content;

    @JsonCreator
    public ChatHistoryMessage(
            @JsonProperty("messageId") long messageId,
            @JsonProperty("senderId") String senderId,
            @JsonProperty("senderName") String senderName,
            @JsonProperty("timestamp") Instant timestamp,
            @JsonProperty("content") String content
    ) {
        if (messageId <= 0) {
            throw new IllegalArgumentException("messageId must be > 0");
        }
        if (Objects.isNull(senderId)) {
            throw new IllegalArgumentException("senderId is null");
        }
        if (senderId.isBlank()) {
            throw new IllegalArgumentException("senderId is blank");
        }
        if (Objects.isNull(senderName)) {
            throw new IllegalArgumentException("senderName is null");
        }
        if (senderName.isBlank()) {
            throw new IllegalArgumentException("senderId is blank");
        }
        if (Objects.isNull(timestamp)) {
            throw new IllegalArgumentException("timestamp is null");
        }
        if (Objects.isNull(content)) {
            throw new IllegalArgumentException("content is null");
        }
        if (content.isBlank()) {
            throw new IllegalArgumentException("content is blank");
        }
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.content = content;
    }
}
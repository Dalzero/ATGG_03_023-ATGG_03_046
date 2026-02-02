package com.joeshim.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * String type: 최종적으로는 문자열로 들고 가는게 가장 안전ㅇㅇ, wire()로 문자열만 넣으면 됨
 *
 * Instant timestamp
 * String sessionId(or UUID)
 * Boolean success
 */
@EqualsAndHashCode
@ToString
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Header {
    private final String type;
    private final Instant timestamp;
    private final String sessionId;
    private final Boolean success;

    @JsonCreator
    public Header(
            @JsonProperty("type") String type,
            @JsonProperty("timestamp") Instant timestamp,
            @JsonProperty("sessionId") String sessionId,
            @JsonProperty("success") Boolean success
            ) {
        if (Objects.isNull(type) || type.isBlank()) {
            throw new IllegalArgumentException("type is null or blank");
        }
        if (Objects.nonNull(sessionId)) {
            if (sessionId.isBlank()) {      // 명시적
                throw new IllegalArgumentException("sessionId is blank");
            }
            try {
                UUID uuid = UUID.fromString(sessionId);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("sessionId is not a valid UUID: %s".formatted(sessionId), e);
            }
        }
        this.type = type;
        this.timestamp = Objects.isNull(timestamp) ? Instant.now() : timestamp;
        this.sessionId = sessionId;
        this.success = success;
    }

    public Header(String type) {
        this(type,null, null, null);
    }

    public Header(String type, String sessionId) {
        this(type,null, sessionId, null);
    }
}

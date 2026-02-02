package com.joeshim.common.dto.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

// code, message
@Getter
@ToString
public final class ErrorBody {
    private final String code;
    private final String message;

    @JsonCreator
    public ErrorBody(
            @JsonProperty("code") String code,
            @JsonProperty("message") String message
    ) {
        if (Objects.isNull(code) || code.isBlank()) {
            throw new IllegalArgumentException("code is null or blank");
        }
        if (Objects.isNull(message) || message.isBlank()) {
            throw new IllegalArgumentException("message is null or blank");
        }
        this.code = code;
        this.message = message;
    }
}

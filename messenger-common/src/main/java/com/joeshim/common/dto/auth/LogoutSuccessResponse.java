package com.joeshim.common.dto.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class LogoutSuccessResponse {
    private final String message;

    @JsonCreator
    public LogoutSuccessResponse(
            @JsonProperty("message") String message
    ) {
        if (Objects.isNull(message)) {
            throw new IllegalArgumentException("message is null");
        }
        if (message.isBlank()) {
            throw new IllegalArgumentException("message is blank");
        }
        this.message = message;
    }
}

package com.joeshim.common.dto.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class LoginSuccessResponse {
    private final String userId;
    private final String message;

    @JsonCreator
    public LoginSuccessResponse(
            @JsonProperty("userId") String userId,
            @JsonProperty("message") String message
    ) {
        if (Objects.isNull(userId)) {
            throw new IllegalArgumentException("userId is null");
        }
        if (userId.isBlank()) {
            throw new IllegalArgumentException("userId is blank");
        }

        if (Objects.isNull(message)) {
            throw new IllegalArgumentException("message is null");
        }
        if (message.isBlank()) {
            throw new IllegalArgumentException("message is blank");
        }
        this.userId = userId;
        this.message = message;
    }
}

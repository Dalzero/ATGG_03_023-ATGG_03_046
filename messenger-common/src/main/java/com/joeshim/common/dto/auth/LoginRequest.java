package com.joeshim.common.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class LoginRequest {

    private final String userId;
    private final String password;

    public LoginRequest(
            @JsonProperty("userId") String userId,
            @JsonProperty("password") String password
    ) {
        if (Objects.isNull(userId)) {
            throw new IllegalArgumentException("userId is null");
        }
        if (userId.isBlank()) {
            throw new IllegalArgumentException("userId is blank");
        }
        if (Objects.isNull(password)) {
            throw new IllegalArgumentException("password is null");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("password is blank");
        }
        this.userId = userId;
        this.password = password;
    }
}

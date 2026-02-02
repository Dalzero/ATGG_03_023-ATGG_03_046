package com.joeshim.common.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import java.util.Objects;

/**
 * 배열 data를 DTO로 변환할 때는 Class가 아니라 TypeReference가 필요함
 * ex)
 * List<UserSummary> users = MAPPER.convertValue(message.getData(), new com.fasterxml.jackson.core.type.TypeReference<List<UserSummary>>() {});
 */
@Getter
@ToString
public class UserSummary {
    private final String id;
    private final String name;
    private final boolean online;

    @JsonCreator
    public UserSummary(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("online") boolean online
    ) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("id is null");
        }
        if (id.isBlank()) {
            throw new IllegalArgumentException("id is blank");
        }
        if (Objects.isNull(name)) {
            throw new IllegalArgumentException("name is null");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("name is blank");
        }
        this.id = id;
        this.name = name;
        this.online = online;
    }
}

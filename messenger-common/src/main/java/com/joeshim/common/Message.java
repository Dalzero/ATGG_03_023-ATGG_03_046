package com.joeshim.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import lombok.Getter;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class Message {
    // Map<String,Object> or JsonNode data
    // addData() 구현은 확장성에 좋지 않음
    private final Header header;
    private final JsonNode data;

    @JsonCreator
    public Message(
            @JsonProperty("header") Header header,
            @JsonProperty("data") JsonNode data
    ) {
        if (Objects.isNull(header)) {
            throw new IllegalArgumentException("header is null");
        }
        this.header = header;
        this.data = Objects.isNull(data) ? NullNode.getInstance() : data;
    }

    public Message(Header header) {
        this(header, NullNode.getInstance());
    }
}
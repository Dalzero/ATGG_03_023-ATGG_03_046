package com.joeshim.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.joeshim.common.type.impl.ChatRoomType;
import com.joeshim.common.type.impl.LoginType;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class CommonTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static JsonNode extractPayloadJson(byte[] framedBytes) throws Exception {
        String all = new String(framedBytes, StandardCharsets.UTF_8);

        int idx = all.indexOf(ProtocolConstants.NEWLINE);
        assertTrue(idx >= 0, "newline not found in framed message");

        String lengthLine = all.substring(0, idx).trim();
        assertTrue(lengthLine.startsWith(ProtocolConstants.LENGTH_PREFIX), "invalid length prefix line: " + lengthLine);

        int newlinePos = -1;
        for (int i = 0; i < framedBytes.length; i++) {
            if (framedBytes[i] == (byte) '\n') {
                newlinePos = i;
                break;
            }
        }
        assertTrue(newlinePos >= 0, "newline byte not found");

        byte[] payload = new byte[framedBytes.length - (newlinePos + 1)];
        System.arraycopy(framedBytes, newlinePos + 1, payload, 0, payload.length);

        return MAPPER.readTree(payload);
    }

    @Test
    void logout_request_should_omit_data_field() throws Exception {
        Header header = new Header(LoginType.LOGOUT.wire());
        Message message = new Message(header); // data는 NullNode

        byte[] framed = PacketUtils.formatMessage(message);
        JsonNode root = extractPayloadJson(framed);

        assertTrue(root.has("header"), "header must exist");
        assertFalse(root.has("data"), "LOGOUT request must NOT contain data field");
    }

    @Test
    void chat_room_list_request_should_have_empty_object_data() throws Exception {
        Header header = new Header(ChatRoomType.CHAT_ROOM_LIST.wire());
        Message message = new Message(header); // data는 NullNode

        byte[] framed = PacketUtils.formatMessage(message);
        JsonNode root = extractPayloadJson(framed);

        assertTrue(root.has("header"), "header must exist");
        assertTrue(root.has("data"), "CHAT-ROOM-LIST request must contain data field");

        JsonNode data = root.get("data");
        assertTrue(data.isObject(), "data must be an object {}");
        assertEquals(0, data.size(), "data object must be empty {}");
    }
}
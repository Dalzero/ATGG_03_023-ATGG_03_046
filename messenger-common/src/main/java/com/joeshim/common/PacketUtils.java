package com.joeshim.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.joeshim.common.type.impl.ChatRoomType;
import com.joeshim.common.type.impl.LoginType;
import com.joeshim.common.type.impl.UserListType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;

/**
 * formatMessage(Message): Message를 JSON UTF-8 bytes로 만들고 "message-length: N\n" 붙여서 전송할 byte[] 생성
 * readMessage(InputStream): 첫 줄에서 N 파싱, N 바이트 정확히 읽기(readFully) JSON bytes → Message로 파싱해 반환
 * [보내기: Message -> bytes]
 * Message를 JSON 바이트로 만듬(UTF-8)
 * 그 바이트 길이 N을 구함
 * message-length: N\n 라는 헤더라인을 만듬
 * 헤더라인 바이트 + JSON 바이트를 붙여서 socket에 씀
 * [받기: InputStream -> Message]
 * \n까지 읽어서 message-length: N 라인을 얻음
 * N을 파싱
 * InputStream에서 정확히 N 바이트를 끝까지 읽음 (readFully)
 * 그 N 바이트(JSON)를 Message로 역직렬화함
 */
public final class PacketUtils {

    private PacketUtils() {}

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final Set<String> OMIT_DATA_FIELD_TYPES = Set.of(
            LoginType.LOGOUT.wire(),
            UserListType.USER_LIST.wire()
    );

    private static final Set<String> EMPTY_OBJECT_DATA_TYPES = Set.of(
            ChatRoomType.CHAT_ROOM_LIST.wire()
    );

    /**
     * payload가 100바이트인데 첫 read에서 30바이트만 읽히면
     * 그 상태로 JSON 파싱을 하면 잘린 JSON이라 역직렬화가 실패하거나, 더 위험하게는 다음 메시지 일부가 섞이는 문제 발생을 예방해주는 method
     * [readFully가 해야하는 일]
     * 지금까지 읽은 양 (오프셋 off)을 0으로 시작함
     * off가 N보다 작은 동안 동안 계속 읽음
     * read -1이면(EOF) 중간에 끊긴거라 예외를 던짐
     * read가 r바이트를 주면 off를 r만큼 증가시킴
     * off == N이 되면 배열을 반환함
     */
    private static byte[] readFully(InputStream in, int n) throws IOException {
        if (n < 0) {
            throw new IllegalArgumentException("n must be >= 0, n: %d".formatted(n));
        }
        byte[] buf = new byte[n];
        int off = 0;
        while (off < n) {
            int r = in.read(buf, off, n - off);  // 이미 읽은 만큼 (off) 뒤부터, 남은 (n-off)만 읽어옴
            if (r == -1) {
                throw new EOFException("unexpected EOF, expected = %d, got = %d".formatted(n, off));
            }
            off += r; // 이번에 실제로 읽은 바이트 수 만큼 위치를 앞으로 밈
        }
        return buf;
    }

    /**
     * 프로토콜 첫 줄: message-length: 123\n
     * \n 전까지를 읽어서 "message-length: 123"을 얻고, 123을 파싱
     * BufferedReader.readLine()을 사용하면 '문자 기반 버퍼'가 InputStream 바이트를 미리 읽어버릴 수 있음, payload N바이트와 경계가 깨질 위험이 있음
     * InputStream을 바이트 단위로 직접 읽는게 안전
     * [readLine]
     * 바이트를 하나씩 읽음
     * \n이 나오면 멈춤
     * 그 전에 읽근 바이트들을 UTF-8 문자열로 만듬
     * 만약 첫 read부터 -1이면 (EOF) null 반환
     */
    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int b = in.read();
        if (b == -1) {
            return null;  // 정상적인 스트림 종료 시에는 Exception ㄴㄴ
        }
        while (b != -1) {
            if (b == '\n') {
                break;
            }
            if (b != '\r') { // \r은 버퍼에 담지 않고 무시 (깔끔한 처리)
                bos.write(b);
            }
            b = in.read();
        }
        return bos.toString(StandardCharsets.UTF_8);
    }

    /**
     * [parseLine]
     * line 앞뒤 공백 제거(trim)
     * prefix가 'message-length:"로 시작하는지 확인
     * prefix 뒤에 남는 문자열을 숫자로 파싱
     * 음수면 예외
     * 숫자 파싱 실패면 예외
     */
    private static int parseLength(String line) {
        if (Objects.isNull(line)) {
            throw new IllegalArgumentException("length line is null");
        }
        String trimmedLine = line.trim();   // CRLF 대비, 공백 제거
        if (!trimmedLine.startsWith(ProtocolConstants.LENGTH_PREFIX)) {
            throw new IllegalArgumentException("invalid length line prefix: %s".formatted(line));
        }
        // substring() : startIndex부터 끝까지 문자열을 반환
        String numberPart = trimmedLine.substring(ProtocolConstants.LENGTH_PREFIX.length()).trim();
        try {
            int n = Integer.parseInt(numberPart);
            if (n < 0) {
                throw new IllegalArgumentException("negative length: %d".formatted(n));
            }
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid length number: %s".formatted(line), e);
        }
    }


    private static byte[] toPayloadBytes(Message message) {
        try {
            ObjectNode root = MAPPER.createObjectNode();
            root.set("header", MAPPER.valueToTree(message.getHeader()));

            String type = message.getHeader().getType();
            JsonNode data = message.getData();

            if (data != null && !data.isNull()) {
                root.set("data", data); // data가 실제로 있으면 그대로 포함
            } else if (EMPTY_OBJECT_DATA_TYPES.contains(type)) {
                root.set("data", MAPPER.createObjectNode());  // data:{}가 필요한 타입
            } else if (OMIT_DATA_FIELD_TYPES.contains(type)) {
                // 문서에서 data 자체가 없는 타입 -> 아무것도 안 넣음
            } else {
                // 기본은 'data 생략'
            }

            return MAPPER.writeValueAsBytes(root);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to serialize message", e);
        }
    }

    /**
     * [readMessage]
     * 길이 라인 한 줄 읽기 (readLine)
     * 그 라인에서 N 추출 (parseLength)
     * N 바이트 payload를 끝까지 읽기 (readFully)
     * payload(JSON)를 끝까지 Message로 변환 (Jackson)
     */
//    public static Message readMessage(InputStream in) throws IOException {
//        if (Objects.isNull(in)) {
//            throw new IllegalArgumentException("InputStream is null");
//        }
//        String line = readLine(in);
//        if (line == null) {
//            throw new EOFException("stream closed before reading length line");
//        }
//        int n = parseLength(line);
//        byte[] payload = readFully(in, n);
//
//        try {
//            // readValue가 실패하면 (잘린 JSON, 깨진 데이터 등) 원인 "프로토콜/통신 문제'일 가능성이 높음
//            return MAPPER.readValue(payload, Message.class);
//        } catch (IOException e) {
//            String debugJson = new String(payload, StandardCharsets.UTF_8);
//            throw new IllegalArgumentException("failed to deserialize message. json: %s".formatted(debugJson), e);
//        }
//    }

    /**
     * Message -> (message-length: N\n + JSON bytes)
     * Message 객체를 JSON 바이트로 만듬 (UTF-8)
     * 그 바이트 길이 N을 구함
     * "message-length: N\n" 라는 길이 라인을 만듬 (바이트)
     * 길이 라인 바이트 + payload 바이트를 하나의 byte[]로 합침
     * 그 byte[]를 socket(OutputStream)에 쓰면 한 메시지 전송 끝
     */
    public static byte[] formatMessage(Message message) {
        if (Objects.isNull(message)) {
            throw new IllegalArgumentException("message is null");
        }
        final byte[] payload = toPayloadBytes(message);

        String headerStr = ProtocolConstants.LENGTH_PREFIX + " " + payload.length + ProtocolConstants.NEWLINE;
        byte[] headerBytes = headerStr.getBytes(StandardCharsets.UTF_8);

        byte[] out = new byte[headerBytes.length + payload.length];
        System.arraycopy(headerBytes, 0, out, 0, headerBytes.length);
        System.arraycopy(payload, 0, out, headerBytes.length, payload.length);
        return out;
    }

    public static InboundPacket readPacket(InputStream in) throws IOException {
        if (Objects.isNull(in)) {
            throw new IllegalArgumentException("InputStream is null");
        }
        String line = readLine(in);
        if (Objects.isNull(line)) {
            throw new EOFException("stream closed before reading length line");
        }
        int n = parseLength(line);
        byte[] payload = readFully(in, n);
        try {
            Message message = MAPPER.readValue(payload, Message.class);
            return new InboundPacket(payload, message);
        } catch (IOException e) {
            throw new IllegalArgumentException("failed to deserialized message", e);
        }
    }
    public static Message readMessage(InputStream in) throws IOException {
        return readPacket(in).message();
    }
}
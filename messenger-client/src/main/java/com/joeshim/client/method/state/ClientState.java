package com.joeshim.client.method.state;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * 클라이언트 런타임 상태
 * userId: 로그인한 사용자 아이디
 * sessionId: 서버가 발급한 세션(UUID 문자열)
 * loggedIn: 로그인 여부
 * currentRoomId: 현재 들어가 있는 방(채팅/파일전송/history 기능에 필요
 * currentRoomName
 * ====================================================================================
 * immutable로 만들고 상태 변화는 login/logout/enterRoom/exitRoom처럼 새 객체 반환으로 처리
 * userId는 서버가 응답 data로 내려주는 스펙이면 거기서 세팅하고, 아니면 로그인 입력값을 그대로 저장하는 방식
 */

@Getter
@ToString
public final class ClientState {

    private final String userId;
    private final String sessionId;
    private final boolean loggedIn;
    private final Long currentRoomId;

    public ClientState(String userId, String sessionId, boolean loggedIn, Long currentRoomId) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.loggedIn = loggedIn;
        this.currentRoomId = currentRoomId;
    }

    public static ClientState empty() {
        return new ClientState(null, null, false, null);
    }

    public ClientState login(String userId, String sessionId) {
        if (Objects.isNull(sessionId)) {
            throw new IllegalArgumentException("sessionId is null");
        }
        if (sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId is blank");
        }
        return new ClientState(userId, sessionId, true, this.currentRoomId);
    }

    public ClientState logout() {
        return new ClientState(this.userId, null, false, null);
    }

    public ClientState enterRoom(Long roomId) {
        if (Objects.isNull(roomId) || roomId <= 0) {
            throw new IllegalArgumentException("roomId is null or <= 0");
        }
        return new ClientState(this.userId, this.sessionId, this.loggedIn, roomId);
    }

    public ClientState exitRoom() {
        return new ClientState(this.userId, this.sessionId, this.loggedIn, null);
    }
}

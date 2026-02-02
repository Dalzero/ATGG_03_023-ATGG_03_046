package com.joeshim.client.method.repo;

import ch.qos.logback.core.net.server.Client;
import com.joeshim.client.method.state.ClientState;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

/**
 * 메모리 기반 클라이언트 상태 저장소
 * 특징:
 * ReceiverLoop(수신 스레드)와 ConsoleLoop(입력 스레드)가 동시에 접근해도 안전하도록 AtomicReference로 상태를 관리함
 * ====================================================================================
 * ReceiverLoop -> ResponseHandler가 로그인 성공 / 로그아웃 성공 등을 받았을 때 상태 갱신
 * ConsoleLoop -> 현재 상태 조회해서 세션이 있는가 체크하고나 roomId를 사용
 */
public final class InMemoryClientStateRepository implements ClientStateRepository {
    private final AtomicReference<ClientState> reference = new AtomicReference<>(ClientState.empty());


    @Override
    public ClientState get() {
        return reference.get();
    }

    @Override
    public void set(ClientState state) {
        if (Objects.isNull(state)) {
            throw new IllegalArgumentException("state is null");
        }
        reference.set(state);
    }

    @Override
    public void update(UnaryOperator<ClientState> updater) {
        if (Objects.isNull(updater)) {
            throw new IllegalArgumentException("under is null");
        }
        reference.updateAndGet(updater);
    }

    @Override
    public void clearSession() {
        reference.updateAndGet(ClientState::logout);
    }
}

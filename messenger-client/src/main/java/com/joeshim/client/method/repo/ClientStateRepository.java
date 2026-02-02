package com.joeshim.client.method.repo;

import com.joeshim.client.method.state.ClientState;

import java.util.function.UnaryOperator;

/**
 * 클라이언트 상태 저장소 추상화
 * 목적:
 * 상태 저장 방식을 InMemory -> (추가구현/확장) 파일/DB/멀티세션 등으로 바꿔도
 * Handler/Server 쪽 코드를 최소 변셩으로 유지하기 위함
 * 사용 규칙
 * - ReceiveLoop(수신 처리)에서 세션/sessionId 갱신이 발생할 수 있고,
 * - ConsoleLoop(입력 처리)에서 상태 조회가 발생하므로 구현체는 thread-safe 해야 함(AtomicReference)
 * =======================================================================================
 * ReceiverLoop(수신)와 ConsoleLoop(입력)가 동시에 상태를 만질 수 있으므로, 저장소는 thread-safe 해야 함
 * update(UnaryOperator)를 둔 이유는 동시성 상황에서 읽고-수정하고-저장을 한 번에 안전하게 처리하기 위해 (AtomicReference updateAndGet 패턴)
 * 구현체는 다음 파일에서 InMemoryClientStateRepository로 만듬
 */
public interface ClientStateRepository {

    // 현재 상태 조회
    ClientState get();

    // 상태 교체
    void set(ClientState state);

    // 원자적 업데이트
    void update(UnaryOperator<ClientState> updater);

    // 세션 정보를 제거 (로그아웃/세션 만료 처리)
    void clearSession();
}

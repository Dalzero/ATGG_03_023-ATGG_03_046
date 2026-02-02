package com.joeshim.client.method.parser.handler.impl;

import com.joeshim.client.method.parser.handler.ResponseHandler;
import com.joeshim.client.method.repo.ClientStateRepository;
import com.joeshim.client.method.view.Printer;
import com.joeshim.common.Message;
import com.joeshim.common.type.impl.LoginType;

import java.util.Objects;

/**
 * LOGOUT_SUCCESS 수신 처리
 * 책임:
 * - ClientStateRepository의 세션 정보 제거
 * - 출력
 * 서버 스펙이 따라 data가 없을 수 있어서, header/type만 보고 처리
 * ============================================================
 * 로그아웃 성공은 세션 제거가 핵심 -> data 파싱 불필요
 *
 */
public final class LogoutSuccessHandler implements ResponseHandler {
    private final ClientStateRepository stateRepository;
    private final Printer printer;

    public LogoutSuccessHandler(ClientStateRepository stateRepository, Printer printer) {
        if (Objects.isNull(stateRepository)) {
            throw new IllegalArgumentException("stateRepository is null");
        }
        if (Objects.isNull(printer)) {
            throw new IllegalArgumentException("printer is null");
        }
        this.stateRepository = stateRepository;
        this.printer = printer;
    }

    @Override
    public boolean supports(String type) {
        return LoginType.LOGOUT_SUCCESS.wire().equals(type);
    }

    @Override
    public void handle(Message message) {
        stateRepository.clearSession();
        printer.info("logout success");
    }
}

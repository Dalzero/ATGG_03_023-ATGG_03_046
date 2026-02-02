package com.joeshim.common.type;

public interface MessageTypeKey {
    // 모든 타입 enum이 공통으로 가진 기능
    // String wire() : header.type에 들어갈 문자열 반환
    String wire();
}

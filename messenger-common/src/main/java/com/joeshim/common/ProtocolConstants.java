package com.joeshim.common;

public class ProtocolConstants {
    // "message-length:", \n 같은 상수 통일
    private ProtocolConstants() {}  // 인스턴스 방지
    public static final String LENGTH_PREFIX = "message-length:";
    public static final String NEWLINE = "\n";

    public static final int PORT = 12345;
}

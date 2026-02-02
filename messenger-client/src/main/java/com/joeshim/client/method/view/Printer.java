package com.joeshim.client.method.view;



// Message를 사람이 읽게 출력하게하는 역할

/**
 * 출력 계층을 한 곳으로 모으는 util
 * 지금은 콘솔, 나중에 GUI
 */
public final class Printer {

    // 사람용 안내/로그는 stderr로 (stdout(JSON) 오염 방지)
    public void info(String message) {
        if (message == null) return;
        System.err.println(message);
    }

    public void warn(String message) {
        if (message == null) return;
        System.err.println("[WARN] " + message);
    }

    public void error(String message) {
        if (message == null) return;
        System.err.println("[ERROR] " + message);
    }

    // JSON 원문 출력은 stdout으로만
    public synchronized void jsonRaw(String rawJson) {
        if (rawJson == null) return;
        System.out.println(rawJson);
    }
}
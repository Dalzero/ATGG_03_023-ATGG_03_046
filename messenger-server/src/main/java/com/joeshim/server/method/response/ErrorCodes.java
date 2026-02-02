package com.joeshim.server.method.response;

/**
 * (선택) - AUTH.INVALID_SESSION 같은 문자열을 상수로 통일(확장성)
 */

public class ErrorCodes {
    public static final String AUTH_INVALID_CREDENTIALS = "AUTH.INVALID_CREDENTIALS";
    public static final String AUTH_INVALID_SESSION = "AUTH.INVALID_SESSION";
    public static final String AUTH_UNAUTHORIZED = "AUTH.UNAUTHORIZED";

    public static final String REQUEST_BAD_REQUEST = "REQUEST.BAD_REQUEST";
    public static final String REQUEST_UNKNOWN_TYPE = "REQUEST.UNKNOWN_TYPE";

    public static final String SERVER_ERROR = "SERVER.ERROR";

    private ErrorCodes() {}
}
package com.joeshim.client.method.parser.handler;

import com.joeshim.client.method.state.ClientState;
import com.joeshim.common.Message;

// 출력은 Printer로 위임
// 나중에 추가: RoomListSuccessHandler, ChatMessageHandler, PrivateMessageHandler, HistoryHandler 등
public interface ResponseHandler {
    boolean supports(String type);
    void handle(Message message);
}

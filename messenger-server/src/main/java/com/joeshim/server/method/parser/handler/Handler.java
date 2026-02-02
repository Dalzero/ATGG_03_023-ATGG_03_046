package com.joeshim.server.method.parser.handler;

import com.joeshim.common.Message;
import com.joeshim.server.thread.channel.ClientConnection;

/**
 * UserListHandler, RoomCreateHandler, RoomListHandler, Enter/ExitHandler, ChatHandler, HistoryHandler 나중에 추가
 */


public interface Handler {
    /**
     * 클라이언트의 요청(request)을 받아 비즈니스 로직을 수행한 뒤,
     * 클라이언트에게 보낼 응답(response)을 반환합니다.
     * * @param request 클라이언트가 보낸 메시지
     * @param channel 클라이언트와의 연결 통로 (IP정보, 전송기능 등 포함)
     * @return 클라이언트에게 전송할 응답 메시지 (응답이 필요 없으면 null)
     */
    Message handle(Message request, ClientConnection channel);
}
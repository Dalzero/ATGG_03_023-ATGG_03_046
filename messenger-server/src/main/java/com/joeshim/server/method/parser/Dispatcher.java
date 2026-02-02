package com.joeshim.server.method.parser;

/**
 * Message의 header.type을 보고 어떤 처리로 넘길지 결정
 * Map<String, Handler> 형태 권장- 확장성 측면
 */

import com.joeshim.common.Message;
import com.joeshim.common.type.impl.LoginType;
import com.joeshim.server.method.parser.handler.Handler;
import com.joeshim.server.method.parser.handler.impl.LoginHandler;
import com.joeshim.server.method.parser.handler.impl.LogoutHandler;
import com.joeshim.server.repo.InMemorySessionRepository;
import com.joeshim.server.service.AuthService;
import com.joeshim.server.thread.channel.ClientConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Dispatcher {
    private final Map<String, Handler> handlers = new HashMap<>();

    public Dispatcher() {
        InMemorySessionRepository sessionRepository = new InMemorySessionRepository();
        AuthService authService = new AuthService(sessionRepository);

        handlers.put(LoginType.LOGIN.wire(), new LoginHandler(authService));
        handlers.put(LoginType.LOGOUT.wire(), new LogoutHandler(authService));
    }

    public Message dispatch(Message request, ClientConnection channel) {
        if (request.getHeader() == null || request.getHeader().getType() == null) {
            log.error(">>> Invalid Request: Header or Type is null");
            return null;
        }

        String type = request.getHeader().getType();
        Handler handler = handlers.get(type);

        if (handler != null) {
            return handler.handle(request, channel);
        } else {
            log.error(">>> Unknown message type: {}", type);
            return null;
        }
    }
}
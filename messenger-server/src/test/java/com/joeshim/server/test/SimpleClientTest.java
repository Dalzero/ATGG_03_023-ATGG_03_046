package com.joeshim.server.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joeshim.common.Header;
import com.joeshim.common.Message;
import com.joeshim.common.ProtocolConstants;
import com.joeshim.common.dto.auth.LoginRequest;
import com.joeshim.common.type.impl.LoginType;
import com.joeshim.common.PacketUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SimpleClientTest {
    public static void main(String[] args) {
        System.out.println(">>> [TestClient] Connecting to server...");

        try (Socket socket = new Socket("localhost", ProtocolConstants.PORT)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            ObjectMapper mapper = new ObjectMapper();

            // -------------------------------------------------------
            // 1. 로그인 요청 보내기 (LOGIN)
            // -------------------------------------------------------
            System.out.println("\n>>> [TestClient] Sending LOGIN request...");

            // 데이터 생성
            LoginRequest loginReq = new LoginRequest("marco", "nhnacademy123");

            // 헤더 생성
            Header loginHeader = new Header(LoginType.LOGIN.wire(), null, null, null);

            // 메시지 조립
            Message loginMessage = new Message(loginHeader, mapper.valueToTree(loginReq));

            // 전송 (PacketUtils가 포맷팅 담당)
            out.write(PacketUtils.formatMessage(loginMessage));
            out.flush();

            // -------------------------------------------------------
            // 2. 로그인 응답 받기
            // -------------------------------------------------------
            Message loginResponse = PacketUtils.readMessage(in);
            System.out.println("<<< [TestClient] Received: " + loginResponse.getHeader().getType());
            System.out.println("    Success: " + loginResponse.getHeader().getSuccess());
            System.out.println("    SessionID: " + loginResponse.getHeader().getSessionId());
            System.out.println("    Data: " + loginResponse.getData());

            String sessionId = loginResponse.getHeader().getSessionId();

            if (sessionId != null) {
                // -------------------------------------------------------
                // 3. 로그아웃 요청 보내기 (LOGOUT) - 로그인 성공 시에만
                // -------------------------------------------------------
                System.out.println("\n>>> [TestClient] Sending LOGOUT request...");

                // 로그아웃 헤더 (세션 ID 필수)
                Header logoutHeader = new Header(LoginType.LOGOUT.wire(), sessionId);
                Message logoutMessage = new Message(logoutHeader); // 데이터 없음

                out.write(PacketUtils.formatMessage(logoutMessage));
                out.flush();

                // -------------------------------------------------------
                // 4. 로그아웃 응답 받기
                // -------------------------------------------------------
                Message logoutResponse = PacketUtils.readMessage(in);
                System.out.println("<<< [TestClient] Received: " + logoutResponse.getHeader().getType());
                System.out.println("    Success: " + logoutResponse.getHeader().getSuccess());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
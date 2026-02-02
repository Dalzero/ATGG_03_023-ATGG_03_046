package com.joeshim.client.ui.controller;

import com.joeshim.client.ui.ClientUiApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField userIdField;
    @FXML private TextField passwordField;

    private ClientUiApp app;

    public void setApp(ClientUiApp app) {
        this.app = app;
    }

    @FXML
    public void handleLogin() {
        String id = userIdField.getText();
        String pw = passwordField.getText();

        // UI 테스트용: 단순히 비어있지만 않으면 통과!
        if (!id.isEmpty() && !pw.isEmpty()) {
            System.out.println("로그인 성공! 메인 화면으로 이동합니다.");
            app.showMainView(); // 화면 전환
        } else {
            // 경고창 띄우기
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("입력 오류");
            alert.setHeaderText(null);
            alert.setContentText("아이디와 비밀번호를 입력해주세요.");
            alert.showAndWait();
        }
    }
}
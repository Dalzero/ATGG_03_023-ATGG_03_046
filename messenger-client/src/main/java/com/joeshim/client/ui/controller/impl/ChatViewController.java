package com.joeshim.client.ui.controller.impl;

import com.joeshim.client.ui.controller.MainController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode; // ✅ 키보드 키 코드 임포트
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class ChatViewController extends BaseSubController {

    private ListView<String> messageList;
    private TextArea inputField;

    public ChatViewController(Pane root, MainController main, ListView<String> messageList, TextArea inputField) {
        super(root, main);
        this.messageList = messageList;
        this.inputField = inputField;
    }

    @Override
    protected void loadList() {
        setupMessageCellFactory();
    }

    // ✅ [수정됨] 키보드 엔터키 기능 추가!
    @Override
    protected void setupEvents() {
        inputField.setOnKeyPressed(event -> {
            // 엔터키가 눌렸을 때
            if (event.getCode() == KeyCode.ENTER) {
                if (event.isShiftDown()) {
                    // [Shift] + [Enter] -> 강제로 줄바꿈 문자 넣기
                    inputField.replaceSelection("\n");
                } else {
                    // [Enter] -> 전송
                    sendMessage();
                }
                // 엔터키의 기본 기능(단순 줄바꿈)은 무조건 막아야 함
                // (안 그러면 전송 후에도 줄바꿈이 들어가거나, 줄바꿈이 두 번 됨)
                event.consume();
            }
        });
    }

    public void enterRoom(String roomName) {
        messageList.getItems().clear();
        messageList.getItems().add("[System] " + roomName + "에 입장했습니다.");
        messageList.scrollTo(messageList.getItems().size() - 1);

        // 입장 시 바로 입력창 포커스
        Platform.runLater(() -> inputField.requestFocus());
    }

    public void sendMessage() {
        String msg = inputField.getText();
        if (msg != null && !msg.trim().isEmpty()) {
            messageList.getItems().add("나: " + msg.trim());
            inputField.clear();
            messageList.scrollTo(messageList.getItems().size() - 1);

            // 전송 후 바로 입력창 포커스 (연속 채팅 가능)
            inputField.requestFocus();
        }
    }

    private void setupMessageCellFactory() {
        messageList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: white;");
                } else {
                    // ✅ [수정됨] 화면에 보여줄 텍스트 (prefix 제거 로직)
                    String displayText = msg;

                    // 스타일 설정을 위한 변수
                    boolean isMyMessage = msg.startsWith("나:");

                    // "나: "로 시작하면 앞의 3글자("나: ")를 잘라내고 내용만 보여줌
                    if (isMyMessage) {
                        try {
                            // "나: 안녕" -> "안녕" (인덱스 3부터 끝까지)
                            displayText = msg.substring(3).trim();
                        } catch (Exception e) {
                            displayText = msg; // 혹시 모를 에러 방지
                        }
                    }

                    Label label = new Label(displayText);
                    label.setWrapText(true);
                    label.setMaxWidth(230);
                    label.setPadding(new Insets(8, 12, 8, 12));
                    HBox hbox = new HBox(label);

                    if (isMyMessage) {
                        // 내 메시지 (오른쪽, 초록색)
                        label.setStyle("-fx-background-color: #32cd32; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-size: 14px;");
                        hbox.setAlignment(Pos.CENTER_RIGHT);
                    } else {
                        // 상대방 메시지 (왼쪽, 회색)
                        label.setStyle("-fx-background-color: #e4e6eb; -fx-text-fill: black; -fx-background-radius: 15; -fx-font-size: 14px;");
                        hbox.setAlignment(Pos.CENTER_LEFT);
                    }

                    setGraphic(hbox);
                    setText(null);
                    setStyle("-fx-background-color: white;");
                }
            }
        });
    }
}
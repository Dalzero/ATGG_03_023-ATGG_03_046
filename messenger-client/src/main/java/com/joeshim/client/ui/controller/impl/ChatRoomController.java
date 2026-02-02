package com.joeshim.client.ui.controller.impl;

import com.joeshim.client.ui.controller.MainController;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

// ✅ BaseSubController 상속!
public class ChatRoomController extends BaseSubController {

    private ListView<String> chatRoomList;

    public ChatRoomController(Pane root, MainController main, ListView<String> chatRoomList) {
        super(root, main); // 부모에게 필수 정보(root, main) 전달
        this.chatRoomList = chatRoomList;
    }

    // 1. 데이터 채우기 구현
    @Override
    protected void loadList() {
        chatRoomList.getItems().clear();
        chatRoomList.getItems().addAll("Marco", "Alice", "개발팀 회의", "NHN Academy");
        for (int i = 1; i <= 15; i++) {
            chatRoomList.getItems().add("채팅방 " + i);
        }
    }

    // 2. 이벤트 연결 구현
    @Override
    protected void setupEvents() {
        chatRoomList.setOnMouseClicked(e -> {
            String selected = chatRoomList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // 메인에게 "채팅방 입장시켜줘"라고 요청
                main.requestEnterChat(selected);
            }
        });
    }
}
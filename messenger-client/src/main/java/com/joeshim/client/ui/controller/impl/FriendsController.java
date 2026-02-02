package com.joeshim.client.ui.controller.impl;

import com.joeshim.client.ui.controller.MainController;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

public class FriendsController extends BaseSubController {

    private ListView<String> listView;

    public FriendsController(Pane root, MainController main, ListView<String> listView) {
        super(root, main); // 부모에게 필수 정보 넘김
        this.listView = listView;
    }

    @Override
    protected void loadList() {
        // 데이터 로드 로직만 남음
        listView.getItems().addAll("김철수", "이영희", "박지민 (접속중)");
    }

    @Override
    protected void setupEvents() {
        // 이벤트 로직만 남음
        listView.setOnMouseClicked(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                main.requestEnterChat(selected);
            }
        });
    }
}
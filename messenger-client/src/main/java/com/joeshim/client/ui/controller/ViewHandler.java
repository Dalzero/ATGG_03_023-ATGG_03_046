package com.joeshim.client.ui.controller;

import javafx.scene.layout.Pane;

public interface ViewHandler {
    // 화면 초기화 (데이터 로딩 등)
    void initializeData();

    // 이 컨트롤러가 관리하는 루트 패널 반환 (메인이 보여주기 위해)
    Pane getRoot();

    // 화면이 활성화될 때 호출 (새로고침 등 필요 시)
    default void onShow() {}
}
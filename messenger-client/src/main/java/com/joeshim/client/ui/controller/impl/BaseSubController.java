package com.joeshim.client.ui.controller.impl;

import com.joeshim.client.ui.controller.MainController;
import com.joeshim.client.ui.controller.ViewHandler;
import javafx.scene.layout.Pane;

// [Template Method Pattern]
// 공통 로직은 여기서 다 처리하고, 각자 다른 부분만 자식에게 맡김
public abstract class BaseSubController implements ViewHandler {

    protected Pane root; // 내 화면 (VBox 등)
    protected MainController main; // 메인 컨트롤러

    public BaseSubController(Pane root, MainController main) {
        this.root = root;
        this.main = main;
    }

    // ViewHandler 인터페이스 구현
    @Override
    public Pane getRoot() {
        return root;
    }

    // [Template Method] 초기화 순서를 강제함 (데이터 로드 -> 이벤트 연결)
    @Override
    public void initializeData() {
        loadList();       // 1. 데이터 채우기 (자식이 구현)
        setupEvents();    // 2. 클릭 이벤트 연결 (자식이 구현)
    }

    // 자식들이 무조건 구현해야 하는 추상 메서드들
    protected abstract void loadList();
    protected abstract void setupEvents();
}
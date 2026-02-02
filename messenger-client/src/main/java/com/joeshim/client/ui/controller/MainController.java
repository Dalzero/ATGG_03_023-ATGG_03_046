package com.joeshim.client.ui.controller;

import com.joeshim.client.ui.controller.impl.ChatRoomController;
import com.joeshim.client.ui.controller.impl.ChatViewController;
import com.joeshim.client.ui.controller.impl.FriendsController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class MainController {

    // 1. 화면 종류 정의 (Enum)
    public enum ViewType {
        FRIENDS,    // 친구 목록
        CHAT_ROOMS, // 채팅방 목록
        CHAT_VIEW   // 대화창
    }

    // FXML 요소 연결
    @FXML private VBox friendsListPane, roomListPane, chatViewPane;
    @FXML private ListView<String> friendsListView, chatRoomList, messageList;
    @FXML private TextArea inputField;
    @FXML private Label headerTitle;

    // 하위 컨트롤러 관리용 맵
    private Map<ViewType, ViewHandler> controllers = new HashMap<>();

    // 대화창 컨트롤러 (메시지 전송 기능 때문에 따로 참조 유지)
    private ChatViewController chatViewController;

    // 드래그 스크롤 상태 변수
    private boolean isScrolling = false;

    @FXML
    public void initialize() {
        // 1. 하위 컨트롤러 생성 및 조립 (생성자에 'this' 전달 중요!)
        // FriendsController 생성
        FriendsController friendsController = new FriendsController(friendsListPane, this, friendsListView);

        // ChatRoomController 생성
        ChatRoomController chatRoomController = new ChatRoomController(roomListPane, this, chatRoomList);

        // ✅ [수정됨] ChatViewController 생성 (파라미터: 루트, 메인, 리스트, 입력창)
        chatViewController = new ChatViewController(chatViewPane, this, messageList, inputField);

        // 2. 맵에 등록 (Enum과 매칭)
        registerController(ViewType.FRIENDS, friendsController);
        registerController(ViewType.CHAT_ROOMS, chatRoomController);
        registerController(ViewType.CHAT_VIEW, chatViewController);

        // 3. 각 컨트롤러 초기화 (데이터 로드, 이벤트 연결 등 Template Method 실행)
        controllers.values().forEach(ViewHandler::initializeData);

        // 4. 드래그 스크롤 기능 장착 (공통 유틸)
        enableDragScrolling(friendsListView);
        enableDragScrolling(chatRoomList);
        enableDragScrolling(messageList);

        // 5. 시작 화면 설정 (친구 목록)
        switchView(ViewType.FRIENDS, "Friends");
    }

    // 맵 등록 헬퍼 메서드
    private void registerController(ViewType type, ViewHandler controller) {
        controllers.put(type, controller);
    }

    // ✅ [통합 화면 전환 메서드]
    public void switchView(ViewType type, String title) {
        // 모든 패널 숨기기
        controllers.values().forEach(c -> c.getRoot().setVisible(false));

        // 원하는 패널만 찾아서 보이기
        ViewHandler target = controllers.get(type);
        if (target != null) {
            target.getRoot().setVisible(true);
            target.onShow(); // 필요 시 화면 갱신 로직 실행
            headerTitle.setText(title);
        }
    }

    // --- [FXML 이벤트 핸들러] ---

    // 탭 1: 친구 목록
    @FXML
    public void showFriendsTab() {
        switchView(ViewType.FRIENDS, "Friends");
    }

    // 탭 2: 채팅방 목록 (FXML 이름: showChatRoomsTab)
    @FXML
    public void showChatRoomsTab() {
        switchView(ViewType.CHAT_ROOMS, "Chats");
    }

    // ✅ [추가됨] 탭 3: 설정 (이게 없어서 에러 났었음)
    @FXML
    public void showSettingsTab() {
        System.out.println("설정 탭 클릭됨 (미구현)");
        // 나중에 설정 화면 만들면: switchView(ViewType.SETTINGS, "Settings");
    }

    // 메시지 전송 버튼
    @FXML
    public void handleSend() {
        // 대화창 컨트롤러에게 위임
        if (chatViewController != null) {
            chatViewController.sendMessage();
        }
    }

    // --- [공용 기능] ---

    // 하위 컨트롤러에서 호출: 채팅방 입장 요청
    public void requestEnterChat(String roomName) {
        switchView(ViewType.CHAT_VIEW, roomName);
        chatViewController.enterRoom(roomName);
    }

    // 드래그 스크롤 유틸리티
    private void enableDragScrolling(ListView<?> listView) {
        final double[] dragContext = new double[3]; // [0]:마우스Y, [1]:스크롤값, [2]:시작Y

        listView.setOnMousePressed(event -> {
            isScrolling = false;
            dragContext[0] = event.getScreenY();
            dragContext[2] = event.getScreenY();
            Node node = listView.lookup(".scroll-bar:vertical");
            if (node instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) node;
                dragContext[1] = bar.getValue();
            }
        });

        listView.setOnMouseDragged(event -> {
            if (Math.abs(event.getScreenY() - dragContext[2]) > 5) {
                isScrolling = true;
            }

            if (isScrolling) {
                Node node = listView.lookup(".scroll-bar:vertical");
                if (node instanceof ScrollBar) {
                    ScrollBar bar = (ScrollBar) node;
                    double deltaY = event.getScreenY() - dragContext[0];

                    // 감도 조절 (1.4로 설정하셨죠?)
                    double sensitivity = 1.4;
                    double change = (deltaY / listView.getHeight() * (bar.getMax() - bar.getMin())) * sensitivity;

                    // 이동할 목표 위치 계산
                    double newValue = dragContext[1] - change;

                    // 1. 맨 위보다 더 올라가려고 하면? -> 맨 위(Min)로 고정
                    if (newValue < bar.getMin()) {
                        newValue = bar.getMin();
                    }

                    // 2. 맨 아래보다 더 내려가려고 하면? -> 맨 아래(Max)로 고정
                    if (newValue > bar.getMax()) {
                        newValue = bar.getMax();
                    }

                    // 안전한 값만 입력!
                    bar.setValue(newValue);
                }
            }
        });
    }
}
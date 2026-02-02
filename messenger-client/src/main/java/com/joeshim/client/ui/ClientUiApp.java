package com.joeshim.client.ui;

import com.joeshim.client.ui.controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientUiApp extends Application {

    private Stage primaryStage;

    //화면 비율
    private static final double WINDOW_WIDTH = 400;
    private static final double WINDOW_HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Joeshim Messenger");

        //채팅방 최소 사이즈 지정
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(700);

        showLoginView();
    }

    public void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/joeshim/client/view/login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setApp(this);

            primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/joeshim/client/view/main.fxml"));
            Parent root = loader.load();

            primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
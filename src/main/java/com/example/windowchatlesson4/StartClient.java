package com.example.windowchatlesson4;

import com.example.windowchatlesson4.controllers.AuthController;
import com.example.windowchatlesson4.controllers.ChatController;
import com.example.windowchatlesson4.server.models.NetWork;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class StartClient extends Application {
    private NetWork netWork;
    public static Stage primaryStage;
    private Stage authStage;
    private ChatController chatController;

    @Override
    public void start(Stage stage) throws IOException {

        primaryStage = stage;
        netWork = new NetWork();
        netWork.connect();

        openAuthDialog();
        createChatDialog();
    }

    private void openAuthDialog() throws IOException {
        FXMLLoader authLoader = new FXMLLoader(StartClient.class.getResource("auth-view.fxml"));

        authStage = new Stage();
        Scene scene = new Scene(authLoader.load());

        authStage.setScene(scene);
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);
        authStage.setTitle("Authentication");
        authStage.setAlwaysOnTop(true);
        authStage.show();

        AuthController authController = authLoader.getController();

        authController.setNetWork(netWork);
        authController.setStartClient(this);
    }

    private void createChatDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartClient.class.getResource("hello-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);

        chatController = fxmlLoader.getController();
        chatController.setNetWork(netWork);

    }

    public static void main(String[] args) {
        launch();
    }

    public void openChatDialog() {
        authStage.close();
        primaryStage.show();
        primaryStage.setTitle(netWork.getUsername());
        netWork.waitMessage(chatController);
        chatController.setUsernameTitle(netWork.getUsername());
    }

    public void showErrorAlert(String title, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(errorMessage);
        alert.show();
    }
}
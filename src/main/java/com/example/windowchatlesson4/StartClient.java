package com.example.windowchatlesson4;

import com.example.windowchatlesson4.controllers.ChatController;
import com.example.windowchatlesson4.models.NetWork;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class StartClient extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(StartClient.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Messenger");
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();

        NetWork netWork = new NetWork();
        ChatController chatController = fxmlLoader.getController();

        chatController.setNetWork(netWork);

        netWork.connect();
        netWork.waitMessage(chatController);

    }

    public static void main(String[] args) {

        launch();

    }
}
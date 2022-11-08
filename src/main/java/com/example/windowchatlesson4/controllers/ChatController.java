package com.example.windowchatlesson4.controllers;

import com.example.windowchatlesson4.models.NetWork;
import com.example.windowchatlesson4.server.EchoServer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ChatController {

    @FXML
    private ListView<String> userList;

    @FXML
    private Button buttonSend;

    @FXML
    private TextField inputField;

    @FXML
    private TextArea chatText;

    @FXML
    void cleanChat() {
        chatText.clear();
    }

    @FXML
    void initialize() {

        userList.setItems(FXCollections.observableArrayList("Тимофей", "Дмитрий", "Диана", "Арман"));

        chatText.setWrapText(true);

        buttonSend.setOnAction(event -> SendMessage());
        inputField.setOnAction(event -> SendMessage());

    }

    private NetWork netWork;

    public void setNetWork(NetWork netWork) {
        this.netWork = netWork;
    }

    public void appendMessage(String message) {

        chatText.appendText(message + "\n");
        chatText.appendText(System.lineSeparator());
        chatText.scrollTopProperty();

    }

    @FXML
    void SendMessage() {

        String message = inputField.getText().trim();
        if (!message.isBlank()) {

            netWork.sendMessage(message);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка Ввода");
            alert.setHeaderText("Строка не должна быть пустой");
            alert.show();
        }
        inputField.clear();
    }

    @FXML
    void printInfo() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("");
        alert.setContentText("Jdk: 17.0\n" + "Над приложением работал Друзяко Дмитрий");
        alert.show();

    }

    @FXML
    void closeWindow() {
        System.exit(0);
    }
}

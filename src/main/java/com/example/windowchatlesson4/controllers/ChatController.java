package com.example.windowchatlesson4.controllers;

import com.example.windowchatlesson4.StartClient;
import com.example.windowchatlesson4.server.models.NetWork;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;
import java.util.function.Predicate;

import static javafx.collections.FXCollections.observableList;

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
    private Label usernameTitle;

    private String online = "<- ";

    private StartClient startClient;

    private String selectedRecipient;

    DataInputStream in;
    DataOutputStream out;


    public ListView<String> getUserList() {
        return userList;
    }

    public void setUserList(ListView<String> userList) {
        this.userList = userList;
    }

    @FXML
    void cleanChat() {
        chatText.clear();
    }

    @FXML
    void initialize() {

        userList.setItems(FXCollections.observableArrayList());

        chatText.setWrapText(true);

        buttonSend.setOnAction(event -> SendMessage());
        inputField.setOnAction(event -> SendMessage());

        userList.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = userList.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                userList.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                }
            });
            return cell;
        });

    }

    private NetWork netWork;

    public void setNetWork(NetWork netWork) {
        this.netWork = netWork;

    }

    public void appendMessage(String message) {

        String timeStamp = DateFormat.getInstance().format(new Date());

        chatText.appendText(timeStamp);
        chatText.appendText(System.lineSeparator());
        chatText.appendText(message);
        chatText.appendText(System.lineSeparator());
        chatText.appendText(System.lineSeparator());


    }

    public void appendServerMessage(String messageServer) {
        chatText.appendText(messageServer);
        chatText.appendText(System.lineSeparator());
        chatText.appendText(System.lineSeparator());
    }

    @FXML
    public void SendMessage() {

        String message = inputField.getText();

        if (!message.isBlank()) {
            chatText.appendText("Я: " + message + "\n" + "\n");
            if (selectedRecipient != null) {
                netWork.sendPrivateMessage(selectedRecipient, message);
            } else {
                netWork.sendMessage(message);
            }

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

    public void setUsernameTitle(String username) {
        this.usernameTitle.setText(username);
    }

    @FXML
    void closeWindow() {
        System.exit(0);
    }

    public StartClient getStartClient() {
        return startClient;
    }

    public void addUserList(String[] usernames) {

        Arrays.sort(usernames);

        for (int i = 0; i < usernames.length; i++) {
            if (usernames[i].equals(netWork.getUsername())) {
                usernames[i] = usernames[i];
            }
        }

        userList.getItems().clear();
        Collections.addAll(userList.getItems(), usernames);
    }
}
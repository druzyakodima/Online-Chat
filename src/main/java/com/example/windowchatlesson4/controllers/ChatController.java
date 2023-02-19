package com.example.windowchatlesson4.controllers;

import com.example.windowchatlesson4.StartClient;
import com.example.windowchatlesson4.server.models.NetWork;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.*;
import java.text.DateFormat;
import java.util.*;

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

    private StartClient startClient;

    private String selectedRecipient;

    private File fileChatHistory = new File("src/main/resources/historyChat/history.txt");

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

        try {
            chatText.appendText(readUsingBufferedReader(fileChatHistory) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }


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

        userList.getItems().clear();
        Collections.addAll(userList.getItems(), usernames);
    }

    private static String readUsingBufferedReader(File fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> listHistory = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            listHistory.add(line);
        }

        int hundredString = listHistory.size() - 100;

        for (int i = hundredString; i < listHistory.size() - 1; i++) {
            stringBuilder.append(listHistory.get(i)).append("\n").append("\n");
        }

        return stringBuilder.toString();

    }
    @FXML
    void SelectFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("\uD83D\uDCCE");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Картинки", "*.jpg", "*.png", "*.gif", "*.bmp", "*.txt", "*.pdf");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(StartClient.primaryStage);
        Image im = new Image(file.toURI().toString());
        ImageView imv = new ImageView(im);

    }
}
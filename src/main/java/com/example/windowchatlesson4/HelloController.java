package com.example.windowchatlesson4;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


public class HelloController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button buttonSend;

    @FXML
    private TextField inputField;

    @FXML
    private TextArea chatText;

    @FXML
    private TableColumn<RowText, String> contactColumn;

    @FXML
    private TableView<RowText> contactTable;

    @FXML
    void cleanField() {
        inputField.clear();
    }

    @FXML
    void cleanChat() {
        chatText.clear();
    }

    @FXML
    void doPushTextToTable() {

        String message = inputField.getText().trim();
        if (message.length() != 0) {
            addMessageToTable(message);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка Ввода");
            alert.setHeaderText("Строка не должна быть пустой");
            alert.show();
        }
        inputField.clear();
    }

    private void addMessageToTable(String message) {

        chatText.appendText(message + "\n");

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

    @FXML
    void initialize() {


        chatText.setWrapText(true);
        buttonSend.setDefaultButton(true);
        chatText.setEditable(false);
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        contactTable.setItems(FXCollections.observableArrayList(
                new RowText("Андрей Воронин"),
                new RowText("Изя Кацман"),
                new RowText("Кэнси Убуката"),
                new RowText("Сельма Нагель"),
                new RowText("Дядя Юра")
        ));
    }

}

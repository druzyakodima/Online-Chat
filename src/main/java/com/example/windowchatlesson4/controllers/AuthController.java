package com.example.windowchatlesson4.controllers;

import com.example.windowchatlesson4.StartClient;
import com.example.windowchatlesson4.server.models.*;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField loginFieldRegister;
    @FXML
    private PasswordField passwordRegister;
    @FXML
    private TextField usernameRegister;
    private NetWork netWork;
    private StartClient startClient;

    @FXML
    public void checkAuth() {

        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.length() == 0 || password.length() == 0) {
            startClient.showErrorAlert("Ошибка ввода", "Поля не должны быть пустыми");
            return;
        }

        String authErrorMessage = netWork.sendAuthMessage(login, password);

        if (authErrorMessage == null) {

            startClient.openChatDialog();
        } else {
            startClient.showErrorAlert("Ошибка аутентификации", authErrorMessage);
        }
    }

    public void checkAuth(String login, String password) {

        if (login == null || password == null) {
            startClient.showErrorAlert("Ошибка ввода при аутентификации", "Поля не заданы");
        }

        if (login == null || password == null) {
            startClient.showErrorAlert("Ошибка ввода", "Поля не должны быть пустыми");
            return;
        }

        String authErrorMessage = netWork.sendAuthMessage(login, password);

        if (authErrorMessage == null) {
            startClient.openChatDialog();
        } else {
            startClient.showErrorAlert("Ошибка аутентификации", authErrorMessage);
        }
    }

    @FXML
    void registerUser() {
        String login = loginFieldRegister.getText().trim();
        String username = usernameRegister.getText().trim();
        String password = passwordRegister.getText().trim();

        if (login.isBlank() || password.isBlank()|| username.isBlank()) {
            startClient.showErrorAlert("Ошибка регистрации", "Поля не должны быть пустыми");
            return;
        }

        String registerError = netWork.sendRegisterMessage(login, password, username);

        if (registerError == null) {

            startClient.showErrorAlert("Поздравляем вы зарегистрировались!", "Добро пожаловать!");
            checkAuth(login, password);
        } else {
            startClient.showErrorAlert("Ошибка регистрации", registerError);
        }
    }

    @FXML
    public void closeAuth() {
        System.exit(0);
    }

    public void setNetWork(NetWork netWork) {
        this.netWork = netWork;
    }

    public void setStartClient(StartClient startClient) {
        this.startClient = startClient;
    }
}

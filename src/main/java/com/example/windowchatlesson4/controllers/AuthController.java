package com.example.windowchatlesson4.controllers;

import com.example.windowchatlesson4.StartClient;
import com.example.windowchatlesson4.server.authentication.DBAuthenticationService;
import com.example.windowchatlesson4.server.models.*;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

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
            startClient.showErrorAlert("Ошибка аутентификации",authErrorMessage);
        }
    }

    @FXML
    void registerUser() {
        String login = loginFieldRegister.getText().trim();
        String username = usernameRegister.getText().trim();
        String password = passwordRegister.getText().trim();

        if (login.length() == 0 || password.length() == 0 || username.length() == 0) {
            startClient.showErrorAlert("Ошибка ввода", "Поля не должны быть пустыми");
            return;
        }

        String authErrorMessage =  netWork.sendRegisterMessage(login, password,username);

        if (authErrorMessage == null) {

            startClient.openChatDialog();
        } else {
            startClient.showErrorAlert("Ошибка регистарции",authErrorMessage);
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

package com.example.windowchatlesson4.server.models;

import com.example.windowchatlesson4.controllers.ChatController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class NetWork {

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authOk"; // + username
    private static final String AUTHERR_CMD_PREFIX = "/authErr"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/cMsg"; // + Msg
    private static final String SERVER_MSG_CMD_PREFIX = "/sMsg"; // + sMsg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/pMsg"; // + sMsg
    private static final String STOP_SERVER_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_CMD_PREFIX = "/end";
    private static final String GET_CLIENTS_CMD = "/get";
    private static final String CHANGE_USERNAME_CMD = "/ch";
    private static final String REGISTER_CMD_PREFIX = "/reg";

    private final String DEFAULT_HOST = "localhost";
    private final int DEFAULT_PORT = 8180;

    private DataOutputStream out;
    private DataInputStream in;

    private final String host;
    private final int port;
    private String username;
    private ChatController chatController;
    private ListView<String> userList;
    private ObservableList<String> names;

    public NetWork(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public NetWork() {
        this.host = DEFAULT_HOST;
        this.port = DEFAULT_PORT;
    }

    public void connect() {

        try {
            Socket socket = new Socket("localhost", 8180);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Соединение не установлено!");
        }
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка при отправке сообщения");
            alert.setHeaderText("Сообщение не отправлено");
            alert.show();
        }
    }

    public synchronized void waitMessage(ChatController chatController) {

        Thread t = new Thread(() -> {
            while (true) {
                try {
                    String message = in.readUTF();
                    if (message.startsWith(CLIENT_MSG_CMD_PREFIX)) {
                        String[] parse = message.split("\\s+", 3);
                        String sender = parse[1];
                        String messageFromSender = parse[2];
                        Platform.runLater(() -> chatController.appendMessage(String.format("%s: %s", sender, messageFromSender)));

                    } else if (message.startsWith(SERVER_MSG_CMD_PREFIX)) {
                        String[] parse = message.split("\\s+", 2);
                        String messageServer = parse[1];

                        Platform.runLater(() -> chatController.appendServerMessage(messageServer));
                    } else if (message.startsWith(GET_CLIENTS_CMD)) {
                        message = message.substring(message.indexOf('[') + 1, message.indexOf(']'));

                        String[] usernames = message.split(", ");
                        Platform.runLater(() -> chatController.addUserList(usernames));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        t.setDaemon(true);
        t.start();

    }

    public String sendAuthMessage(String login, String password) {
        try {

            out.writeUTF(String.format("%s %s %s", AUTH_CMD_PREFIX, login, password));
            String response = in.readUTF();
            this.username = response.split("\\s", 2)[1];
            if (response.startsWith(AUTHOK_CMD_PREFIX)) {
                this.username = response.split("\\s", 2)[1];
                return null;
            } else {
                return response.split("\\s", 2)[1];
            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendPrivateMessage(String selectedRecipient, String message) {
        sendMessage(String.format("%s %s %s", PRIVATE_MSG_CMD_PREFIX, selectedRecipient, message));
    }


    public String sendRegisterMessage(String login, String password, String username) {

        try {
            out.writeUTF(String.format("%s %s %s %s", REGISTER_CMD_PREFIX, login, password, username));
            String response = in.readUTF();
            this.username = response.split("\\s", 2)[1];
            if (response.startsWith(AUTHOK_CMD_PREFIX)) {
                this.username = response.split("\\s", 2)[1];
                return null;
            } else {
                return response.split("\\s", 2)[1];
            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    public void setUsername(String username) {
        this.username = username;
    }
}


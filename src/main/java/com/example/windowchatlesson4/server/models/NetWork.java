package com.example.windowchatlesson4.server.models;

import com.example.windowchatlesson4.controllers.ChatController;
import javafx.scene.control.Alert;
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

    private final String DEFAULT_HOST = "localhost";
    private final int DEFAULT_PORT = 8180;

    private DataOutputStream out;
    private DataInputStream in;

    private final String host;
    private final int port;

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

    public void waitMessage(ChatController chatController) {
        Thread t = null;
        t = new Thread(() -> {
            while (true) {
                try {
                    String message = in.readUTF();
                    chatController.appendMessage(message);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        t.setDaemon(true);
        t.start();

    }
}

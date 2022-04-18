package com.example.windowchatlesson4.server.handler;

import com.example.windowchatlesson4.controllers.ChatController;
import com.example.windowchatlesson4.server.EchoServer;
import com.example.windowchatlesson4.server.authentication.AuthenticationService;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.*;


public class ClientHandler {

    private static final String AUTH_CMD_PREFIX = "/auth "; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authOk "; // + username
    private static final String AUTHERR_CMD_PREFIX = "/authErr "; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/cMsg "; // + Msg
    private static final String SERVER_MSG_CMD_PREFIX = "/sMsg "; // + sMsg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/pMsg"; // + sMsg
    private static final String STOP_SERVER_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_CMD_PREFIX = "/end ";
    private static final String GET_CLIENTS_CMD = "/get";
    private static final String CHANGE_USERNAME_CMD = "/ch";
    private static final String REGISTER_CMD_PREFIX = "/reg";
    private static final String REGISTER_OK_CMD_PREFIX = "/regOk";

    private ObservableList<String> names;
    private EchoServer echoServer;
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    ChatController chatController = new ChatController();
    private Logger file = Logger.getLogger("file");

    public ClientHandler(EchoServer EchoServer, Socket clientSocket) {
        this.echoServer = EchoServer;
        this.clientSocket = clientSocket;
    }

    public ClientHandler() {

    }

    public void handle() throws IOException {
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());

        new Thread(() -> {
            try {
                try {
                    authenticationAndRegister();
                    readMassage();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    echoServer.subscribe(this);
                }
            } catch (IOException e) {
                echoServer.unSubscribe(this);

                try {
                    echoServer.broadCastClientsDisconnected(this);
                } catch (IOException ex) {
                    ex.printStackTrace();

                }
                System.out.println("Клиент отключился!" + "\n" + "Ожидание клиента...");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();

    }

    public void setUsername(String username) {
        this.username = username;
    }

    private void authenticationAndRegister() throws IOException, SQLException, ClassNotFoundException {

        while (true) {
            String message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                boolean isSuccessAuth = processAuthentication(message);
                if (isSuccessAuth) {
                    break;
                }

            } else if (message.startsWith(REGISTER_CMD_PREFIX)) {
                processRegister(message);

            } else {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка аутентификации");
                System.out.println("Неудачная попытка аутентификации");
                file.warn("Неудачная попытка аутентификации");
            }
        }
    }

    private boolean processRegister(String message) throws IOException {
        String[] parse = message.split("\\s+", 4);
        if (parse.length != 4) {
            out.writeUTF(REGISTER_CMD_PREFIX + "Ошибка регистрации");
            return false;
        }

        String login = parse[1];
        String password = parse[2];
        String usernameClient = parse[3];

        AuthenticationService auth = echoServer.getAuthenticationService();


        if (auth.checkLoginByFree(login)) {
            auth.createUser(login, password, usernameClient);
            out.writeUTF(REGISTER_OK_CMD_PREFIX);
            file.info("Зарегистрировался новый пользователь " + usernameClient);
            return true;
        } else {

            out.writeUTF(REGISTER_CMD_PREFIX + " Пользователь с таким логином уже существует");
            return false;
        }
    }


    private boolean processAuthentication(String message) throws IOException, SQLException, ClassNotFoundException {
        String[] parse = message.split("\\s+", 3);
        if (parse.length != 3) {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка аутентификации");
            file.warn("Ошибка аутентификации");
            return false;
        }

        String login = parse[1];
        String password = parse[2];

        AuthenticationService auth = echoServer.getAuthenticationService();

        username = auth.getUsernameByLoginAndPassword(login, password);

        if (username != null) {
            if (echoServer.isUsernameBusy(username)) {
                out.writeUTF(String.format("Логин %s уже используется", login));
                return false;
            }

            out.writeUTF(AUTHOK_CMD_PREFIX + " " + username);

            connectUser(username);

            return true;
        } else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " " + "Неверный логин или пароль");
            return false;
        }
    }

    private void connectUser(String username) throws IOException {
        echoServer.subscribe(this);
        System.out.println("Пользователь " + username + " подключился к чату");
        echoServer.broadCastClients(this);
    }

    private void readMassage() throws IOException {

        while (true) {
            String message = in.readUTF();

            System.out.println("Сообщение | " + username + ": " + message);
            if (message.startsWith(STOP_SERVER_CMD_PREFIX)) {
                System.exit(0);
            } else if (message.startsWith(END_CLIENT_CMD_PREFIX)) {
                return;
            } else if (message.startsWith(PRIVATE_MSG_CMD_PREFIX)) {
                echoServer.privateMessage(message, this);
            } else if (message.startsWith(CHANGE_USERNAME_CMD)) {
                echoServer.changeUsername(message, this);
            } else {
                echoServer.broadcastMessage(message, this);
            }
        }
    }

    public void sendMessage(String sender, String message) throws IOException {
        if (sender != null) {
            out.writeUTF(String.format("%s %s %s", CLIENT_MSG_CMD_PREFIX, sender, message));
        } else {
            out.writeUTF(String.format("%s %s", SERVER_MSG_CMD_PREFIX, message));
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendClientsList(List<ClientHandler> clients) throws IOException {
        String message = String.format("%s %s", GET_CLIENTS_CMD, clients.toString());
        out.writeUTF(message);
        System.out.println(message);
    }

    @Override
    public String toString() {
        return username;
    }

    public void sendServerMessage(String message) throws IOException {
        out.writeUTF(String.format("%s %s", SERVER_MSG_CMD_PREFIX, message));
    }
}

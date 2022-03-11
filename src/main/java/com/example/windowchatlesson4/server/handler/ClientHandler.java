package com.example.windowchatlesson4.server.handler;

import com.example.windowchatlesson4.server.EchoServer;
import com.example.windowchatlesson4.server.authentication.AuthenticationService;
import com.example.windowchatlesson4.server.models.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ClientHandler {

    private static final String AUTH_CMD_PREFIX = "/auth "; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authOk "; // + username
    private static final String AUTHERR_CMD_PREFIX = "/authErr "; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/cMsg "; // + Msg
    private static final String SERVER_MSG_CMD_PREFIX = "/sMsg "; // + sMsg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/pMsg"; // + sMsg
    private static final String STOP_SERVER_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_CMD_PREFIX = "/end ";

    private EchoServer echoServer;
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

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
                authentication();
                readMassage();
            } catch (IOException e) {
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.out.println("Клиент отключился!" + "\n" + "Ожидание клиента...");

            }
        }).start();
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                boolean isSuccessAuth = processAuthentication(message);
                if (isSuccessAuth) {
                    break;
                }
            } else {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка аутентификации");
                System.out.println("Неудачная попытка аутентификации");
            }
        }
    }

    private boolean processAuthentication(String message) throws IOException {
        String[] parse = message.split("\\s+");
        if (parse.length != 3) {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка аутентификации");
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

            out.writeUTF(String.format("Пользователь %s подключился к чату...", username));
            echoServer.subscribe(this);

            System.out.println("Пользователь " + username + " подключился к чату");
            return true;
        } else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " " + "Неверный логин или пароль");
            return false;
        }
    }

    private void readMassage() throws IOException {
        while (true) {
            String message = in.readUTF();
            System.out.println("Сообщение | " + username + ": " + message);
            if (message.startsWith(STOP_SERVER_CMD_PREFIX)) {
                System.exit(1);
            } else if (message.startsWith(END_CLIENT_CMD_PREFIX)) {
                return;

            } else if (message.startsWith(PRIVATE_MSG_CMD_PREFIX)) {
                echoServer.privateMessage(message, this);
            } else {
                echoServer.broadcastMessage(message, this);
            }
        }
    }

    public void sendMessage(String sender, String message) throws IOException {
        out.writeUTF(String.format("%s: %s", sender, message));
    }

    public String getUsername() {
        return username;
    }
}

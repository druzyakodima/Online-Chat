package com.example.windowchatlesson4.server;

import com.example.windowchatlesson4.controllers.ChatController;
import com.example.windowchatlesson4.server.authentication.AuthenticationService;
import com.example.windowchatlesson4.server.authentication.BaseAuthentication;
import com.example.windowchatlesson4.server.authentication.DBAuthenticationService;
import com.example.windowchatlesson4.server.handler.ClientHandler;
import com.example.windowchatlesson4.server.models.NetWork;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class EchoServer {
    private ClientHandler clientHandler = new ClientHandler();
    private final ServerSocket serverSocket;
    private final AuthenticationService authenticationService;
    private final List<ClientHandler> clients;
    NetWork netWork = new NetWork();
    private List<ClientHandler> clientsChangeName = new ArrayList<>();


    public EchoServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        authenticationService = new DBAuthenticationService();
        clients = new ArrayList<>();

    }

    public void start() {
        System.out.println("СЕРВЕР ЗАПУЩЕН!");
        System.out.println("-------------------");
        try {
            while (true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void waitAndProcessNewClientConnection() throws IOException {

        System.out.println("Ожидание клиента...");
        Socket socket = serverSocket.accept();
        System.out.println("Клиент подключился!");

        processClientConnection(socket);
    }


    private void processClientConnection(Socket socket) throws IOException {
        ClientHandler handler = new ClientHandler(this, socket);
        handler.handle();
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        clientsChangeName.add(clientHandler);
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println(clients);
    }

    public synchronized boolean isUsernameBusy(String username) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender, boolean isServerMessage) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendMessage(isServerMessage ? null : sender.getUsername(), message);
        }
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        broadcastMessage(message, sender, false);

    }

    public void changeUsername(String message, ClientHandler sender) throws IOException {
        String[] parse = message.split(" ", 3);
        String login = parse[1];
        String username = parse[2];
        String oldName = sender.getUsername();
        DBAuthenticationService dbAuthenticationService = new DBAuthenticationService();

        new Thread(() -> {
            try {
                dbAuthenticationService.updateUsername(login, username);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            for (ClientHandler client : clients) {

                if (client.getUsername().equals(sender.getUsername())) {

                    client.setUsername(username);
                    netWork.setUsername(username);

                    int index = clientsChangeName.indexOf(sender);
                    clientsChangeName.set(index, client);
                }
                try {
                    client.sendClientsList(clientsChangeName);
                    client.sendServerMessage(String.format("%s поменял имя на %s", oldName, username
                    ));

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void privateMessage(String message, ClientHandler sender) {

        String[] parse = message.split("\\s+", 3);
        String username = parse[1];
        String privateMessage = parse[2];
        new Thread(() -> {
            for (ClientHandler client : clients) {
                if (client.getUsername().equals(username)) {
                    try {
                        client.sendMessage(sender.getUsername(), privateMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public synchronized void broadCastClients(ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {

            client.sendServerMessage(String.format("%s присоединился к чату", sender.getUsername()
            ));
            client.sendClientsList(clients);
        }
    }

    public synchronized void broadCastClientsDisconnected(ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendServerMessage(String.format("%s отключился", sender.getUsername()));
            client.sendClientsList(clients);
        }

    }
}


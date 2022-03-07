package com.example.windowchatlesson4.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class EchoServer {

    private static final int SERVER_PORT = 8180;
    private static DataInputStream in;
    private static DataOutputStream out;
    Scanner scanner = new Scanner(System.in);
    ServerSocket serverSocket;

    public EchoServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() {
        try {
            while (true) {
                System.out.println("Ожидание подключения...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Соединение установлено!");

                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                sendMessageServer();

                try {
                    while (true) {
                        String message = in.readUTF();
                        if (message.equals("/server-stop")) {
                            System.out.println("Сервер остановлен");
                            System.exit(0);
                        }

                        System.out.println("Клиент: " + message);
                        out.writeUTF("Я: " + message.toUpperCase());

                    }
                } catch (SocketException e) {
                    clientSocket.close();
                    System.out.println("Клиент отключился");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageServer() {

        Thread tServerSend = null;
        tServerSend = new Thread(() -> {

            while (true) {
                try {

                    synchronized (this) {
                        String messageServer = "Сервер: " + scanner.nextLine();
                        out.writeUTF(messageServer);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        tServerSend.setDaemon(true);
        tServerSend.start();

    }


}

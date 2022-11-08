package com.example.windowchatlesson4;

import com.example.windowchatlesson4.server.EchoServer;

import java.io.IOException;

public class ServerApp {

    private static final int SERVER_PORT = 8180;

    public static void main(String[] args) {

        try {
            new EchoServer(SERVER_PORT).start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
package com.example.windowchatlesson4;

import com.example.windowchatlesson4.server.EchoServer;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;

public class ServerApp {

    private static final int SERVER_PORT = 8180;

    public static void main(String[] args) {

        PropertyConfigurator.configure("src/main/resources/logs/configs/log4j.properties");
        try {
            new EchoServer(SERVER_PORT).start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
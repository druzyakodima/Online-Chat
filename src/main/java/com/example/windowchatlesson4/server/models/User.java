package com.example.windowchatlesson4.server.models;
import lombok.Data;

@Data
public class User {
    private final String login;
    private final String password;
    private final String username;
}
package com.example.windowchatlesson4.server.authentication;

public interface AuthenticationService {

    String getUsernameByLoginAndPassword(String login, String password);

    void startAuthentication();
    void endAuthentication();
 }

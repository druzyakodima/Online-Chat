package com.example.windowchatlesson4.server.authentication;

import java.sql.SQLException;

public interface AuthenticationService {

    String getUsernameByLoginAndPassword(String login, String password) throws SQLException, ClassNotFoundException;

    void startAuthentication() throws ClassNotFoundException, SQLException;
    void endAuthentication() throws SQLException;
 }

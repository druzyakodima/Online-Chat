package com.example.windowchatlesson4.server.authentication;

import java.sql.*;

public class DBAuthenticationService implements AuthenticationService {

    public static final String SQLITE_SRC = "jdbc:sqlite:src/main/resources/db/mainDB.db";
    private static Connection connection;
    private static Statement stmt;
    private static ResultSet rs;

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        String passwordDB = null;
        String username = null;

        startAuthentication();

        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM auth WHERE login = ?");
            pstmt.setString(1, login);
            rs = pstmt.executeQuery();

            if (rs.isClosed()) {
                return null;
            }

            username = rs.getString("username");
            passwordDB = rs.getString("password");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ((passwordDB != null) && (passwordDB.equals(password))) ? username : null;
    }

    @Override
    public void startAuthentication() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_SRC);
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public Boolean checkLoginByFree(String login) {
        String username = null;
        startAuthentication();
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM auth WHERE login = ?");
            rs = pstmt.executeQuery();
            if (rs.isClosed()) {
                return true;
            }

            username = rs.getString("username");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return username == null;
    }

    @Override
    public void createUser(String login, String password, String username) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO auth (login, password, username) VALUES (?, ?, ?)");

            pstmt.setString(1, login);
            pstmt.setString(2, password);
            pstmt.setString(3, username);

            pstmt.addBatch();

            pstmt.executeBatch();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void updateUsername(String login, String newUsername) {
        try {
            stmt.executeUpdate(String.format("UPDATE auth SET username = '%s' WHERE login = '%s'", newUsername, login));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endAuthentication() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}



package com.example.windowchatlesson4.server.authentication;

import java.sql.*;

public class DBAuthenticationService implements AuthenticationService {

    private static Connection connection;
    private static Statement stmt;
    private ResultSet rs;


    @Override
    public String getUsernameByLoginAndPassword(String login, String password) throws SQLException, ClassNotFoundException {

        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/db/mainDB.db");
        stmt = connection.createStatement();

        rs = stmt.executeQuery(String.format("SELECT * FROM auth WHERE login = '%s'", login));

        if (rs.isClosed()) {
            return null;
        }

        String username = rs.getString("username");
        String passwordDB = rs.getString("password");

        return ((passwordDB != null) && (passwordDB.equals(password))) ? username : null;

    }

    @Override
    public void startAuthentication() throws ClassNotFoundException, SQLException {
    }

    @Override
    public void endAuthentication() throws SQLException {
        connection.close();
    }

    public String insertUsername(String login, String password, String username)  {
        try {

           stmt.executeUpdate(String.format("INSERT INTO auth (login, password, username) VALUES ('user%s', '%s', " + "'username%s')", login, password, username));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }

   public  void updateUsername(String login, String username) throws SQLException {
        stmt.executeUpdate(String.format("UPDATE auth SET username = '%s' WHERE login = '%s'", username, login));
    }

}



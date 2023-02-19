package com.example.windowchatlesson4.server.authentication;
import com.example.windowchatlesson4.server.models.User;

import java.util.List;

public class BaseAuthentication implements AuthenticationService {

    private static final List<User> clients = List.of(
            new User("timofey", "1111", "Тимофей"),
            new User("dmitriy", "2222", "Дмитрий"),
            new User("diana", "3333", "Диана"),
            new User("arman", "4444", "Арман")
    );

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User client : clients) {
            if (client.getLogin().equals(login) && client.getPassword().equals(password)) {
                return client.getUsername();
            }
        }
        return null;
    }

    @Override
    public Boolean checkLoginByFree(String login) {
        return false;
    }

    @Override
    public void createUser(String login, String password, String username) {

    }

    @Override
    public void updateUsername(String login, String newUsername) {

    }

    @Override
    public void startAuthentication() {
        System.out.println("Старт аутентификации");
    }

    @Override
    public void endAuthentication() {
        System.out.println("Конец аутентификации");
    }
}

package com.example.windowchatlesson4.server.authentication;

import com.example.windowchatlesson4.server.models.User;
import java.util.ArrayList;
import java.util.List;

public class BaseAuthentication implements AuthenticationService {

    private static final List<User> clients = List.of(
            new User("erick", "1111", "Эрик_Картман"),
            new User("stan", "2222", "Стэн_Марш"),
            new User("kenny", "3333", "Кенни_Маккормик")
    );

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User client: clients){
            if (client.getLogin().equals(login) && client.getPassword().equals(password)){
                return client.getUsername();
            }
        }
        return null;
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

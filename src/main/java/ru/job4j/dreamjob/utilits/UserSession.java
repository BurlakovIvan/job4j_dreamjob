package ru.job4j.dreamjob.utilits;

import ru.job4j.dreamjob.model.User;

import javax.servlet.http.HttpSession;

public final class UserSession {
    private UserSession() {
    }

    public static User user(HttpSession session) {
        User userSession = (User) session.getAttribute("user");
        if (userSession == null) {
            userSession = new User();
            userSession.setName("Гость");
        }
        return userSession;
    }
}

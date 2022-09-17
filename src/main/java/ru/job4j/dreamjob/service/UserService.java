package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.store.UserDbStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@ThreadSafe
public class UserService {
    private final UserDbStore store;

    public UserService(UserDbStore store) {
        this.store = store;
    }

    public List<User> findAll() {
        return new ArrayList<>(store.findAll());
    }

    public Optional<User> add(User user) {
        return store.add(user);
    }
}

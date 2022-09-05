package ru.practicum.shareit.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserRepositoryImpl {

    private final HashMap<Long, User> users = new HashMap<>();

    public User add(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public User getById(Long userId) {
        return users.get(userId);
    }

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    public void delete(Long userId) {
        users.remove(userId);
    }
}

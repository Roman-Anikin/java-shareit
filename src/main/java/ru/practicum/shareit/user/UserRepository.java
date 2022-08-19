package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    User add(User user);

    User update(User user);

    User getById(Long userId);

    List<User> getAll();

    void delete(Long userId);

}

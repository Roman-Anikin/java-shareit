package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    User add(User user);

    User update(Long id, User user);

    User getById(Long userId);

    List<User> getAll();

    void delete(Long userId);

}

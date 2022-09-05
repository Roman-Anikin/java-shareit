package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto add(UserDto user);

    UserDto update(Long id, UserDto user);

    UserDto getById(Long userId);

    List<UserDto> getAll();

    void delete(Long userId);
}

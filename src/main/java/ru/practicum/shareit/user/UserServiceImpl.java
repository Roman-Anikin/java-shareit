package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository repository, UserMapper userMapper) {
        this.repository = repository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto add(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty() || userDto.getEmail().isBlank()) {
            throw new ValidationException("Почта не может быть пустой");
        }
        User user = userMapper.convertFromDto(userDto);
        log.info("Добавлен пользователь {}", user);
        return userMapper.convertToDto(repository.save(user));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User newUser = checkUser(userId);
        if (userDto.getName() != null) {
            newUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            newUser.setEmail(userDto.getEmail());
        }
        log.info("Обновлен пользователь {}", newUser);
        return userMapper.convertToDto(repository.save(newUser));
    }

    @Override
    public UserDto getById(Long userId) {
        User user = checkUser(userId);
        log.info("Получен пользователь {}", user);
        return userMapper.convertToDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = new ArrayList<>(repository.findAll());
        log.info("Получен список пользователей {}", users);
        return users.stream()
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        checkUser(userId);
        log.info("Удален пользователь {}", getById(userId));
        repository.deleteById(userId);
    }

    private User checkUser(Long userId) {
        Optional<User> user = repository.findById(userId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь с id " + userId + " не найден");
        }
        return user.get();
    }
}

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
        User user = userMapper.convertFromDto(userDto);
        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            throw new ValidationException("Почта не может быть пустой");
        }
        log.info("Добавлен пользователь {}", user);
        return userMapper.convertToDto(repository.save(user));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User newUser = userMapper.convertFromDto(getById(userId));
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
        Optional<User> user = repository.findById(userId);
        if (user.isPresent()) {
            log.info("Получен пользователь {}", user.get());
            return userMapper.convertToDto(user.get());
        }
        throw new ObjectNotFoundException("Пользователь с id " + userId + " не найден");
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = new ArrayList<>();
        repository.findAll().forEach(users::add);
        log.info("Получен список пользователей {}", users);
        return users.stream()
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        if (getById(userId) != null) {
            log.info("Удален пользователь {}", getById(userId));
            repository.deleteById(userId);
        }
    }
}

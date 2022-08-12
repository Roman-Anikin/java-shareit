package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectAlreadyExistException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private Long id = 1L;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User add(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            throw new ValidationException("Почта не может быть пустой");
        }
        checkEmail(user.getEmail());
        user.setId(id++);
        log.info("Добавлен пользователь {}", user);
        return repository.add(user);
    }

    @Override
    public User update(Long userId, User user) {
        User newUser = getById(userId);
        if (newUser != null) {
            if (user.getName() != null) {
                newUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                checkEmail(user.getEmail());
                newUser.setEmail(user.getEmail());
            }
            log.info("Обновлен пользователь {}", newUser);
            return repository.update(newUser);
        }
        throw new ObjectNotFoundException("Пользователь с id " + userId + " не найден");
    }

    @Override
    public User getById(Long userId) {
        User user = repository.getById(userId);
        log.info("Получен пользователь {}", user);
        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> users = repository.getAll();
        log.info("Получен список пользователей {}", users);
        return users;
    }

    @Override
    public void delete(Long userId) {
        if (getById(userId) != null) {
            log.info("Удален пользователь {}", getById(userId));
            repository.delete(userId);
        }
    }

    private void checkEmail(String email) {
        for (User user : getAll()) {
            if (user.getEmail().equals(email)) {
                throw new ObjectAlreadyExistException("Пользователь с почтой " + email + " уже существует");
            }
        }
    }
}

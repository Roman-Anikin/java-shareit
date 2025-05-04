package shareit.app.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shareit.app.exception.ObjectNotFoundException;
import shareit.app.exception.ValidationException;
import shareit.app.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public UserDto add(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty() || userDto.getEmail().isBlank()) {
            throw new ValidationException("Почта не может быть пустой");
        }
        User user = userMapper.convertFromDto(userDto);
        repository.save(user);
        log.info("Добавлен пользователь {}", user);
        return userMapper.convertToDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User user = checkUser(userId);
        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
        log.info("Обновлен пользователь {}", user);
        return userMapper.convertToDto(user);
    }

    @Override
    public UserDto getById(Long userId) {
        User user = checkUser(userId);
        log.info("Получен пользователь {}", user);
        return userMapper.convertToDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = repository.findAll();
        log.info("Получен список пользователей {}", users);
        return userMapper.convertToDto(users);
    }

    @Override
    public void delete(Long userId) {
        User user = checkUser(userId);
        repository.deleteById(userId);
        log.info("Удален пользователь {}", user);
    }

    private User checkUser(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() ->
                        new ObjectNotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
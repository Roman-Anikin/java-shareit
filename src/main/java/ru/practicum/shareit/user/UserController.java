package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userConverter) {
        this.userService = userService;
        this.userMapper = userConverter;
    }

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        return userMapper.convertToDto(userService.add(userMapper.convertFromDto(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @RequestBody UserDto userDto, @PathVariable Long userId) {
        return userMapper.convertToDto(userService.update(userId, userMapper.convertFromDto(userDto)));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        return userMapper.convertToDto(userService.getById(userId));
    }

    @GetMapping
    public List<UserDto> getAll() {
        List<User> users = userService.getAll();
        return users.stream()
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());
    }
}

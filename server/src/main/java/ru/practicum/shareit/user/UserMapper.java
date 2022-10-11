package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapper {

    public User convertFromDto(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}

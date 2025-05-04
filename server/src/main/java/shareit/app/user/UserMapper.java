package shareit.app.user;

import org.springframework.stereotype.Component;
import shareit.app.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User convertFromDto(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public List<UserDto> convertToDto(List<User> users) {
        return users
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}

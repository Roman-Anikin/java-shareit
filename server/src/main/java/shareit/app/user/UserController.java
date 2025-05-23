package shareit.app.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shareit.app.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public UserDto add(@RequestBody UserDto userDto) {
        return service.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long userId) {
        return service.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        service.delete(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        return service.getById(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAll();
    }
}

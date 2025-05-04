package shareit.app.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import shareit.app.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private UserClient client;

    @PostMapping
    public Mono<UserDto> add(@Valid @RequestBody UserDto userDto) {
        return client.add(userDto);
    }

    @PatchMapping("/{userId}")
    public Mono<UserDto> update(@Valid @RequestBody UserDto userDto, @PathVariable Long userId) {
        return client.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public Mono<Void> delete(@PathVariable Long userId) {
        return client.delete(userId);
    }

    @GetMapping("/{userId}")
    public Mono<UserDto> getById(@PathVariable Long userId) {
        return client.getById(userId);
    }

    @GetMapping
    public Mono<List<UserDto>> getAll() {
        return client.getAll();
    }
}

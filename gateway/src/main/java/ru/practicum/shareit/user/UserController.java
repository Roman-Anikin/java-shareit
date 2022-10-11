package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserClient client;

    @PostMapping
    public ResponseEntity<UserDto> add(@Valid @RequestBody UserDto userDto) {
        return client.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@Valid @RequestBody UserDto userDto, @PathVariable Long userId) {
        return client.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        client.delete(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getById(@PathVariable Long userId) {
        return client.getById(userId);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return client.getAll();
    }
}

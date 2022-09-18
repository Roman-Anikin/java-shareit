package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql")
@Sql(scripts = "/delete-data.sql", executionPhase = AFTER_TEST_METHOD)
public class UserIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository repository;

    @BeforeEach
    public void setUp() {
        UserDto user = new UserDto(null, "name", "qwe@qwerty.com");
        UserDto user2 = new UserDto(null, "name2", "asd@qwerty.com");
        userService.add(user);
        userService.add(user2);
    }

    @Test
    public void create() {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        userService.add(user);

        Optional<User> foundUser = repository.findById(3L);
        assertThat(foundUser.get().getId()).isEqualTo(3L);
        assertThat(foundUser.get().getName()).isEqualTo(user.getName());
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void update() {
        UserDto user = new UserDto(2L, "qwerty", "update@qwerty.com");
        userService.update(2L, user);

        Optional<User> foundUser = repository.findById(2L);
        assertThat(foundUser.get().getId()).isEqualTo(2L);
        assertThat(foundUser.get().getName()).isEqualTo(user.getName());
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void getById() {
        Optional<User> foundUser = repository.findById(1L);
        assertThat(foundUser.get().getId()).isEqualTo(1L);
        assertThat(foundUser.get().getName()).isEqualTo("name");
        assertThat(foundUser.get().getEmail()).isEqualTo("qwe@qwerty.com");
    }

    @Test
    public void getAll() {
        List<User> users = repository.findAll();
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getId()).isEqualTo(1L);
        assertThat(users.get(0).getName()).isEqualTo("name");
        assertThat(users.get(1).getId()).isEqualTo(2L);
        assertThat(users.get(1).getName()).isEqualTo("name2");
    }

    @Test
    public void delete() {
        userService.delete(1L);

        Optional<User> foundUser = repository.findById(1L);
        assertThat(foundUser).isEmpty();
    }
}

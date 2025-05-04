package shareit.app.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import shareit.app.exception.ObjectNotFoundException;
import shareit.app.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbc;

    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    public void setUp() {
        cleanDatabase();

        user1 = userService.add(new UserDto(null, "name", "qwe@qwerty.com"));
        user2 = userService.add(new UserDto(null, "name2", "asd@qwerty.com"));
    }

    @Test
    public void create() {
        UserDto user = userService.add(new UserDto(null, "name", "mail@qwerty.com"));

        UserDto foundUser = userService.getById(user.getId());
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getName()).isEqualTo(user.getName());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void update() {
        UserDto user = new UserDto(user1.getId(), "qwerty", "update@qwerty.com");
        userService.update(user.getId(), user);

        UserDto foundUser = userService.getById(user1.getId());
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getName()).isEqualTo(user.getName());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void getById() {
        UserDto foundUser = userService.getById(user1.getId());
        assertThat(foundUser.getId()).isEqualTo(user1.getId());
        assertThat(foundUser.getName()).isEqualTo("name");
        assertThat(foundUser.getEmail()).isEqualTo("qwe@qwerty.com");
    }

    @Test
    public void getAll() {
        List<UserDto> users = userService.getAll();
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getId()).isEqualTo(user1.getId());
        assertThat(users.get(0).getName()).isEqualTo("name");
        assertThat(users.get(1).getId()).isEqualTo(user2.getId());
        assertThat(users.get(1).getName()).isEqualTo("name2");
    }

    @Test
    public void delete() {
        userService.delete(user1.getId());

        assertThatThrownBy(() -> userService.getById(user1.getId()))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    void cleanDatabase() {
        jdbc.execute("DELETE FROM bookings");
        jdbc.execute("DELETE FROM comments");
        jdbc.execute("DELETE FROM items");
        jdbc.execute("DELETE FROM requests");
        jdbc.execute("DELETE FROM users");
    }
}

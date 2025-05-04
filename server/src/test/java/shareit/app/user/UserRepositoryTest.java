package shareit.app.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    private User user;

    @BeforeEach
    void setUp() {
        cleanDatabase();

        user = repository.save(new User(null, "test", "test@mail.com"));
    }

    @Test
    void save() {
        User newUser = repository.save(new User(null, "new", "new@mail.com"));

        assertThat(newUser.getId()).isNotNull();
        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    void saveWithExistingMail() {
        assertThatThrownBy(() ->
                repository.save(new User(null, "test2", "test@mail.com")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void update() {
        user.setName("updated");
        repository.save(user);

        User updated = repository.findById(user.getId()).get();
        assertThat(updated.getName()).isEqualTo("updated");
    }

    @Test
    void findAll() {
        repository.save(new User(null, "second", "second@mail.com"));

        assertThat(repository.findAll())
                .hasSize(2)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("test@mail.com", "second@mail.com");
    }

    @Test
    void delete() {
        repository.deleteById(user.getId());

        assertThat(repository.count()).isZero();
    }

    void cleanDatabase() {
        jdbc.execute("DELETE FROM bookings");
        jdbc.execute("DELETE FROM comments");
        jdbc.execute("DELETE FROM items");
        jdbc.execute("DELETE FROM requests");
        jdbc.execute("DELETE FROM users");
    }
}

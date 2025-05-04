package shareit.app.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import shareit.app.booking.Booking;
import shareit.app.booking.BookingStatus;
import shareit.app.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager manager;

    @Autowired
    private JdbcTemplate jdbc;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        cleanDatabase();

        owner = manager.persist(new User(null, "user 1", "qwe@mail.com"));
        booker = manager.persist(new User(null, "user 2", "asd@mail.com"));
        item = manager.persist(new Item(null, "item 1", "desc 1", true, owner, null));
        booking = manager.persist(new Booking(null, getLTD(2), getLTD(3), item, booker,
                BookingStatus.APPROVED));
        comment = manager.persist(new Comment(null, "text", item, booker, getLTD(3)));
    }

    @Test
    public void findAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getId()).isEqualTo(comment.getId());
        assertThat(comments.get(0).getText()).isEqualTo(comment.getText());
    }

    private LocalDateTime getLTD(int sec) {
        return LocalDateTime.now().plusSeconds(sec).truncatedTo(ChronoUnit.SECONDS);
    }

    void cleanDatabase() {
        jdbc.execute("DELETE FROM bookings");
        jdbc.execute("DELETE FROM comments");
        jdbc.execute("DELETE FROM items");
        jdbc.execute("DELETE FROM requests");
        jdbc.execute("DELETE FROM users");
    }
}

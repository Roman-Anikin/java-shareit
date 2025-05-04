package shareit.app.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import shareit.app.user.User;
import shareit.app.util.OffsetPageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager manager;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private JdbcTemplate jdbc;

    private User user1;
    private User user2;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    public void setUp() {
        cleanDatabase();

        user1 = manager.persist(new User(null, "requester 1", "qwe@mail.com"));
        user2 = manager.persist(new User(null, "requester 2", "asd@mail.com"));
        request1 = manager.persist(new ItemRequest(null, "text 2", user2, null));
        request2 = manager.persist(new ItemRequest(null, "text 3", user2, null));
    }

    @Test
    public void findAllByRequesterId() {
        List<ItemRequest> requests = requestRepository.findAllByRequesterId(user2.getId(), Sort.unsorted());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getId()).isEqualTo(request1.getId());
        assertThat(requests.get(0).getDescription()).isEqualTo(request1.getDescription());
        assertThat(requests.get(1).getId()).isEqualTo(request2.getId());
        assertThat(requests.get(1).getDescription()).isEqualTo(request2.getDescription());
    }

    @Test
    public void findAllByRequesterIdNot() {
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdNot(user1.getId(), Pageable.unpaged());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getId()).isEqualTo(request1.getId());
        assertThat(requests.get(0).getDescription()).isEqualTo(request1.getDescription());
        assertThat(requests.get(1).getId()).isEqualTo(request2.getId());
        assertThat(requests.get(1).getDescription()).isEqualTo(request2.getDescription());
    }

    @Test
    public void findAllByRequesterIdNotWithPagination() {
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdNot(user1.getId(),
                new OffsetPageRequest(1, 1, Sort.unsorted()));

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getId()).isEqualTo(request2.getId());
        assertThat(requests.get(0).getDescription()).isEqualTo(request2.getDescription());
    }

    void cleanDatabase() {
        jdbc.execute("DELETE FROM bookings");
        jdbc.execute("DELETE FROM comments");
        jdbc.execute("DELETE FROM items");
        jdbc.execute("DELETE FROM requests");
        jdbc.execute("DELETE FROM users");
    }
}

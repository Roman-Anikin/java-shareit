package shareit.app.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import shareit.app.requests.ItemRequest;
import shareit.app.user.User;
import shareit.app.util.OffsetPageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository repository;

    @Autowired
    private TestEntityManager manager;

    @Autowired
    private JdbcTemplate jdbc;

    private User firstUser;
    private User secondUser;
    private ItemRequest request;
    private Item item;
    private Item item2;
    private Item item3;

    @BeforeEach
    public void setUp() {
        cleanDatabase();

        firstUser = manager.persist(new User(null, "user", "qwe@mail.com"));
        secondUser = manager.persist(new User(null, "user2", "asd@mail.com"));
        request = manager.persist(new ItemRequest(null, "desc", secondUser, LocalDateTime.now()));
        item = manager.persist(new Item(null, "item", "desc", true, firstUser, request));
        item2 = manager.persist(
                new Item(null, "second item", "desc2", true, firstUser, null));
        item3 = manager.persist(
                new Item(null, "third item", "desc3", true, secondUser, null));
    }

    @Test
    public void getByOwnerId() {
        List<Item> items = repository.getAllByOwnerId(secondUser.getId(), Pageable.unpaged());
        assertThat(items).hasSize(1);
        assertThat(items).usingRecursiveComparison().isEqualTo(List.of(item3));
    }

    @Test
    public void getByOwnerIdWithPagination() {
        List<Item> items = repository.getAllByOwnerId(firstUser.getId(),
                new OffsetPageRequest(1, 1, Sort.unsorted()));
        assertThat(items).hasSize(1);
        assertThat(items).usingRecursiveComparison().isEqualTo(List.of(item2));
    }

    @Test
    public void searchByTextInName() {
        List<Item> items = repository.getAllByText("sec", Pageable.unpaged());
        assertThat(items).hasSize(1);
        assertThat(items).usingRecursiveComparison().isEqualTo(List.of(item2));
    }

    @Test
    public void searchByTextInDescription() {
        List<Item> items = repository.getAllByText("ird", Pageable.unpaged());
        assertThat(items).hasSize(1);
        assertThat(items).usingRecursiveComparison().isEqualTo(List.of(item3));
    }

    @Test
    public void searchByTextWithPagination() {
        List<Item> items = repository.getAllByText("it", new OffsetPageRequest(2, 2, Sort.unsorted()));
        assertThat(items).hasSize(1);
        assertThat(items).usingRecursiveComparison().isEqualTo(List.of(item3));
    }

    @Test
    public void getAllByRequestId() {
        List<Item> items = repository.getAllByRequestId(request.getId());
        assertThat(items).hasSize(1);
        assertThat(items).usingRecursiveComparison().isEqualTo(List.of(item));
    }

    void cleanDatabase() {
        jdbc.execute("DELETE FROM bookings");
        jdbc.execute("DELETE FROM comments");
        jdbc.execute("DELETE FROM items");
        jdbc.execute("DELETE FROM requests");
        jdbc.execute("DELETE FROM users");
    }
}

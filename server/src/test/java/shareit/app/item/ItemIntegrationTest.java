package shareit.app.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import shareit.app.booking.BookingService;
import shareit.app.booking.dto.BookingDto;
import shareit.app.item.dto.CommentDto;
import shareit.app.item.dto.ItemDto;
import shareit.app.item.dto.OwnerItemDto;
import shareit.app.requests.ItemRequestService;
import shareit.app.requests.dto.ItemRequestDto;
import shareit.app.user.UserService;
import shareit.app.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRequestService requestService;
    @Autowired
    private JdbcTemplate jdbc;

    private UserDto user1;
    private UserDto user2;
    private ItemRequestDto request;
    private ItemDto item1;
    private ItemDto item2;

    @BeforeEach
    public void setUp() {
        cleanDatabase();

        user1 = userService.add(new UserDto(null, "user 1", "mail@qwerty.com"));
        user2 = userService.add(new UserDto(null, "user 2", "qwe@qwerty.com"));

        request = requestService.add(user2.getId(),
                new ItemRequestDto(null, "request", null, null));

        item1 = itemService.add(user1.getId(),
                new ItemDto(null, "item 1", "desc 1", true, null));
        item2 = itemService.add(user1.getId(),
                new ItemDto(null, "item 2", "desk 2", true, request.getId()));

        bookingService.add(user2.getId(), new BookingDto(null, LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2), null, item1.getId(), null, null, null));
    }

    @Test
    public void create() {
        ItemDto item = itemService.add(user1.getId(),
                new ItemDto(null, "item 3", "desc 3", true, null));

        OwnerItemDto foundItem = itemService.getById(user1.getId(), item.getId());
        assertThat(foundItem.getId()).isEqualTo(item.getId());
        assertThat(foundItem.getName()).isEqualTo(item.getName());
        assertThat(foundItem.getDescription()).isEqualTo(item.getDescription());
        assertThat(foundItem.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    public void update() {
        ItemDto item = itemService.update(item1.getId(), user1.getId(),
                new ItemDto(null, "item 3", "desc 3", true, null));

        OwnerItemDto foundItem = itemService.getById(user1.getId(), item.getId());
        assertThat(foundItem.getId()).isEqualTo(item.getId());
        assertThat(foundItem.getName()).isEqualTo(item.getName());
        assertThat(foundItem.getDescription()).isEqualTo(item.getDescription());
        assertThat(foundItem.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    public void getById() {
        OwnerItemDto foundItem = itemService.getById(user1.getId(), item1.getId());
        assertThat(foundItem.getId()).isEqualTo(item1.getId());
        assertThat(foundItem.getName()).isEqualTo(item1.getName());
        assertThat(foundItem.getDescription()).isEqualTo(item1.getDescription());
    }

    @Test
    public void getByOwner() {
        List<OwnerItemDto> items = itemService.getByOwner(user1.getId(), 0, 5);
        assertThat(items).hasSize(2);
        assertThat(items.get(0).getId()).isEqualTo(item1.getId());
        assertThat(items.get(0).getName()).isEqualTo(item1.getName());
        assertThat(items.get(1).getId()).isEqualTo(item2.getId());
        assertThat(items.get(1).getName()).isEqualTo(item2.getName());
    }

    @Test
    public void searchByText() {
        List<ItemDto> items = itemService.searchByText(user1.getId(), "1", 0, 5);
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getId()).isEqualTo(item1.getId());
        assertThat(items.get(0).getName()).isEqualTo(item1.getName());
        assertThat(items.get(0).getDescription()).isEqualTo(item1.getDescription());
    }

    @Test
    public void addComment() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        CommentDto comment = itemService.addComment(item1.getId(), user2.getId(),
                new CommentDto(null, "text", null, null));

        OwnerItemDto foundItem = itemService.getById(user2.getId(), item1.getId());
        assertThat(foundItem.getComments()).hasSize(1);
        assertThat(foundItem.getComments().get(0).getId()).isEqualTo(comment.getId());
        assertThat(foundItem.getComments().get(0).getText()).isEqualTo(comment.getText());
        assertThat(foundItem.getComments().get(0).getAuthorName()).isEqualTo(user2.getName());
        assertThat(foundItem.getComments().get(0).getCreated()).isNotNull();
    }

    @Test
    public void getAllByRequestId() {
        List<ItemDto> items = itemService.getAllByRequestId(request.getId());
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getId()).isEqualTo(item2.getId());
        assertThat(items.get(0).getName()).isEqualTo(item2.getName());
    }

    void cleanDatabase() {
        jdbc.execute("DELETE FROM bookings");
        jdbc.execute("DELETE FROM comments");
        jdbc.execute("DELETE FROM items");
        jdbc.execute("DELETE FROM requests");
        jdbc.execute("DELETE FROM users");
    }
}

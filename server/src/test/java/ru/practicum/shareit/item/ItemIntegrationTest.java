package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql")
@Sql(scripts = "/delete-data.sql", executionPhase = AFTER_TEST_METHOD)
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
    private ItemRepository repository;

    @BeforeEach
    public void setUp() {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        UserDto user2 = new UserDto(null, "user2", "qwe@qwerty.com");
        ItemRequestDto request = new ItemRequestDto(null, "request", null, null);
        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        ItemDto item2 = new ItemDto(null, "new item", "new desc", true, 1L);
        BookingDto bookingDto = new BookingDto(null, LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2), null, 1L, null, null, null);
        userService.add(user);
        userService.add(user2);
        requestService.add(2L, request);
        itemService.add(1L, item);
        itemService.add(1L, item2);
        bookingService.add(2L, bookingDto);
    }

    @Test
    public void create() {
        ItemDto item = new ItemDto(null, "item2", "desc2", true, null);
        itemService.add(1L, item);

        Optional<Item> foundItem = repository.findById(3L);
        assertThat(foundItem.get().getId()).isEqualTo(3L);
        assertThat(foundItem.get().getName()).isEqualTo(item.getName());
        assertThat(foundItem.get().getDescription()).isEqualTo(item.getDescription());
        assertThat(foundItem.get().getAvailable()).isEqualTo(item.getAvailable());
        assertThat(foundItem.get().getOwner().getId()).isEqualTo(1L);
        assertThat(foundItem.get().getRequest()).isNull();
    }

    @Test
    public void update() {
        ItemDto item = new ItemDto(null, "update", "update desc", false, null);
        itemService.update(1L, 1L, item);

        Optional<Item> foundItem = repository.findById(1L);
        assertThat(foundItem.get().getId()).isEqualTo(1L);
        assertThat(foundItem.get().getName()).isEqualTo(item.getName());
        assertThat(foundItem.get().getDescription()).isEqualTo(item.getDescription());
        assertThat(foundItem.get().getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    public void getById() {
        Optional<Item> foundItem = repository.findById(1L);
        assertThat(foundItem.get().getId()).isEqualTo(1L);
        assertThat(foundItem.get().getName()).isEqualTo("item");
        assertThat(foundItem.get().getDescription()).isEqualTo("desc");
    }

    @Test
    public void getByOwner() {
        List<Item> items = repository.getAllByOwnerId(1L, Pageable.unpaged());
        assertThat(items).hasSize(2);
        assertThat(items.get(0).getId()).isEqualTo(1L);
        assertThat(items.get(0).getName()).isEqualTo("item");
        assertThat(items.get(1).getId()).isEqualTo(2L);
        assertThat(items.get(1).getName()).isEqualTo("new item");
    }

    @Test
    public void searchByText() {
        List<Item> items = repository.getAllByText("ne", Pageable.unpaged());
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getId()).isEqualTo(2L);
        assertThat(items.get(0).getName()).isEqualTo("new item");
    }

    @Test
    public void addComment() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        CommentDto commentDto = new CommentDto(null, "text", null, null);
        itemService.addComment(1L, 2L, commentDto);

        OwnerItemDto foundItem = itemService.getById(2L, 1L);
        assertThat(foundItem.getComments()).hasSize(1);
        assertThat(foundItem.getComments().get(0).getId()).isEqualTo(1L);
        assertThat(foundItem.getComments().get(0).getText()).isEqualTo(commentDto.getText());
        assertThat(foundItem.getComments().get(0).getAuthorName()).isEqualTo("user2");
        assertThat(foundItem.getComments().get(0).getCreated()).isNotNull();
    }

    @Test
    public void getAllByRequestId() {
        List<Item> items = repository.getAllByRequestId(1L);
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getId()).isEqualTo(2L);
        assertThat(items.get(0).getName()).isEqualTo("new item");
    }
}

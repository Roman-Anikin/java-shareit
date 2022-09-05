package ru.practicum.shareit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql")
@Sql(scripts = "/delete-data.sql", executionPhase = AFTER_TEST_METHOD)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String url = "/items";

    @Test
    public void create() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")));
    }

    @Test
    public void createWithoutHeader() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createWithoutOwner() throws Exception {
        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createWithoutAvailable() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", null, null);
        mockMvc.perform(postRequest(item, 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createWithoutName() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, null, "desc", true, null);
        mockMvc.perform(postRequest(item, 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createWithoutDescription() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", null, true, null);
        mockMvc.perform(postRequest(item, 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        ItemDto item2 = new ItemDto(null, "update", "update desc", false, null);
        mockMvc.perform(patchRequest(item2, 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("update")))
                .andExpect(jsonPath("$.description", is("update desc")))
                .andExpect(jsonPath("$.available", is(false)));
    }

    @Test
    public void updateWithoutHeader() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        ItemDto item2 = new ItemDto(null, "update", "update desc", false, null);
        mockMvc.perform(MockMvcRequestBuilders.patch(url + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(item2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateWithWrongOwner() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        ItemDto item2 = new ItemDto(null, "update", "update desc", false, null);
        mockMvc.perform(patchRequest(item2, 1L, 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateOnlyAvailable() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        ItemDto item2 = new ItemDto(null, null, null, false, null);
        mockMvc.perform(patchRequest(item2, 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")))
                .andExpect(jsonPath("$.description", is("desc")))
                .andExpect(jsonPath("$.available", is(false)));
    }

    @Test
    public void updateOnlyName() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        ItemDto item2 = new ItemDto(null, "new item", null, true, null);
        mockMvc.perform(patchRequest(item2, 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("new item")))
                .andExpect(jsonPath("$.description", is("desc")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    public void updateOnlyDescription() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        ItemDto item2 = new ItemDto(null, null, "new desc", true, null);
        mockMvc.perform(patchRequest(item2, 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")))
                .andExpect(jsonPath("$.description", is("new desc")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    public void getById() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")))
                .andExpect(jsonPath("$.description", is("desc")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    public void getByOwner() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "new user", "qwer@qwerty.com");
        mockMvc.perform(postRequest(user2));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        ItemDto item2 = new ItemDto(null, "new item", "new desc", false, null);
        mockMvc.perform(postRequest(item2, 1L));

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("item")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("new item")));

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void searchByName() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/search")
                        .param("text", "iTe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("item")));
    }

    @Test
    public void searchByDescription() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/search")
                        .param("text", "Esc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("item")));
    }

    @Test
    public void searchNotAvailable() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", false, null);
        mockMvc.perform(postRequest(item, 1L));

        ItemDto item2 = new ItemDto(null, "new item", "new desc", true, null);
        mockMvc.perform(postRequest(item2, 1L));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/search")
                        .param("text", "dEs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].name", is("new item")));
    }

    @Test
    public void getByIdWithBookings() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking.id", is(1)))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(2)));
    }

    @Test
    public void getAllWithBookings() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        UserDto user3 = new UserDto(null, "asdf", "zxc@qwerty.com");
        mockMvc.perform(postRequest(user3));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        ItemDto item2 = new ItemDto(null, "item2", "desc", true, null);
        mockMvc.perform(postRequest(item2, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(2L);
        bookingDto1.setStart(time.plusDays(8));
        bookingDto1.setEnd(time.plusDays(10));
        mockMvc.perform(postRequest(bookingDto1, 3L));

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("item")))
                .andExpect(jsonPath("$[0].lastBooking", nullValue()))
                .andExpect(jsonPath("$[0].nextBooking.id", is(1)))
                .andExpect(jsonPath("$[0].nextBooking.bookerId", is(2)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("item2")))
                .andExpect(jsonPath("$[1].lastBooking", nullValue()))
                .andExpect(jsonPath("$[1].nextBooking.id", is(2)))
                .andExpect(jsonPath("$[1].nextBooking.bookerId", is(3)));
    }

    @Test
    public void getWithoutComments() throws Exception {
        UserDto user = new UserDto(null, "user", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")))
                .andExpect(jsonPath("$.description", is("desc")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.comments", hasSize(0)));
    }

    @Test
    public void addEmptyComment() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        mockMvc.perform(postRequest(bookingDto, 2L));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        CommentDto comment = new CommentDto();
        mockMvc.perform(postRequest(comment, 2L, 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addComment() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        mockMvc.perform(postRequest(bookingDto, 2L));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        CommentDto comment = new CommentDto();
        comment.setText("comment");
        mockMvc.perform(postRequest(comment, 2L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("comment")))
                .andExpect(jsonPath("$.authorName", is("qwerty")));
    }

    @Test
    public void getWithCommentAndBookings() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        mockMvc.perform(postRequest(bookingDto, 2L));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        CommentDto comment = new CommentDto();
        comment.setText("comment");
        mockMvc.perform(postRequest(comment, 2L, 1L));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")))
                .andExpect(jsonPath("$.lastBooking.id", is(1)))
                .andExpect(jsonPath("$.nextBooking", nullValue()))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id", is(1)))
                .andExpect(jsonPath("$.comments[0].text", is("comment")))
                .andExpect(jsonPath("$.comments[0].authorName", is("qwerty")));
    }

    private @NotNull MockHttpServletRequestBuilder postRequest(UserDto user) throws JsonProcessingException {
        return MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(user));
    }

    private @NotNull MockHttpServletRequestBuilder postRequest(ItemDto item,
                                                               Long ownerId) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(url)
                .header("X-Sharer-User-Id", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(item));
    }

    private @NotNull MockHttpServletRequestBuilder patchRequest(ItemDto item,
                                                                Long itemId,
                                                                Long ownerId) throws JsonProcessingException {
        return MockMvcRequestBuilders.patch(url + "/" + itemId)
                .header("X-Sharer-User-Id", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(item));
    }

    private @NotNull MockHttpServletRequestBuilder postRequest(BookingDto booking,
                                                               Long userId) throws JsonProcessingException {
        return MockMvcRequestBuilders.post("/bookings")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(booking));
    }

    private @NotNull MockHttpServletRequestBuilder postRequest(CommentDto comment,
                                                               Long userId,
                                                               Long itemId) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(url + "/" + itemId + "/comment")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(comment));
    }
}

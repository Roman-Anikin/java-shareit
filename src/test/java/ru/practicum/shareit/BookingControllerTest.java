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
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql")
@Sql(scripts = "/delete-data.sql", executionPhase = AFTER_TEST_METHOD)
public class BookingControllerTest {

    private final String url = "/bookings";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void create() throws Exception {
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
        mockMvc.perform(postRequest(bookingDto, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is("2022-10-04T00:00:00")))
                .andExpect(jsonPath("$.end", is("2022-10-08T00:00:00")))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.itemId", is(1)))
                .andExpect(jsonPath("$.booker.id", is(2)))
                .andExpect(jsonPath("$.bookerId", is(2)))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    public void createWithUnavailableItem() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        ItemDto item = new ItemDto(null, "item", "desc", false, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createWithoutExistUser() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createWithoutExistItem() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createWithStartInPast() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        ItemDto item = new ItemDto(null, "item", "desc", false, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.minusDays(1));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createWithEndInPast() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        ItemDto item = new ItemDto(null, "item", "desc", false, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.minusDays(4));
        mockMvc.perform(postRequest(bookingDto, 2L))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createWithEndIsBeforeStart() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        ItemDto item = new ItemDto(null, "item", "desc", false, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(2));
        mockMvc.perform(postRequest(bookingDto, 2L))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createWithUserIsOwner() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void setApprove() throws Exception {
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

        mockMvc.perform(patchRequest(1L, "true", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.item.name", is("item")))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    public void setReject() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url + "/1")
                        .param("approved", "false")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.item.name", is("item")))
                .andExpect(jsonPath("$.status", is(BookingStatus.REJECTED.toString())));
    }

    @Test
    public void getById() throws Exception {
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
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.item.name", is("item")));
    }

    @Test
    public void getAllForUser() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        UserDto user3 = new UserDto(null, "asdf", "zxc@qwerty.com");
        mockMvc.perform(postRequest(user3));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStart(time.plusDays(8));
        bookingDto1.setEnd(time.plusDays(10));
        mockMvc.perform(postRequest(bookingDto1, 3L));

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].start", is("2022-10-04T00:00:00")));
    }

    @Test
    public void getAllForWrongUser() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("X-Sharer-User-Id", 20))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllForUserWithAllState() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        UserDto user3 = new UserDto(null, "asdf", "zxc@qwerty.com");
        mockMvc.perform(postRequest(user3));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStart(time.plusDays(8));
        bookingDto1.setEnd(time.plusDays(10));
        mockMvc.perform(postRequest(bookingDto1, 3L));

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].start", is("2022-10-04T00:00:00")));
    }

    @Test
    public void getAllForUserWithFutureState() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        UserDto user3 = new UserDto(null, "asdf", "zxc@qwerty.com");
        mockMvc.perform(postRequest(user3));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStart(time.plusDays(8));
        bookingDto1.setEnd(time.plusDays(10));
        mockMvc.perform(postRequest(bookingDto1, 3L));

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .param("state", "FUTURE")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].start", is("2022-10-04T00:00:00")));
    }

    @Test
    public void getAllForUserWithWaitingState() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        UserDto user3 = new UserDto(null, "asdf", "zxc@qwerty.com");
        mockMvc.perform(postRequest(user3));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStart(time.plusDays(8));
        bookingDto1.setEnd(time.plusDays(10));
        mockMvc.perform(postRequest(bookingDto1, 3L));

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].start", is("2022-10-04T00:00:00")));
    }

    @Test
    public void getAllForUserWithRejectedState() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        UserDto user3 = new UserDto(null, "asdf", "zxc@qwerty.com");
        mockMvc.perform(postRequest(user3));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStart(time.plusDays(8));
        bookingDto1.setEnd(time.plusDays(10));
        mockMvc.perform(postRequest(bookingDto1, 3L));

        mockMvc.perform(patchRequest(1L, "false", 1L));

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .param("state", "REJECTED")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].start", is("2022-10-04T00:00:00")));
    }

    @Test
    public void getAllForUserWithWrongState() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        UserDto user3 = new UserDto(null, "asdf", "zxc@qwerty.com");
        mockMvc.perform(postRequest(user3));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStart(time.plusDays(8));
        bookingDto1.setEnd(time.plusDays(10));
        mockMvc.perform(postRequest(bookingDto1, 3L));

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .param("state", "WRONG")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllForOwner() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        UserDto user3 = new UserDto(null, "asdf", "zxc@qwerty.com");
        mockMvc.perform(postRequest(user3));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStart(time.plusDays(8));
        bookingDto1.setEnd(time.plusDays(10));
        mockMvc.perform(postRequest(bookingDto1, 3L));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].start", is("2022-10-09T00:00:00")))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].start", is("2022-10-04T00:00:00")));
    }

    @Test
    public void getAllForWrongOwner() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/owner")
                        .header("X-Sharer-User-Id", 20))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllForOwnerWithAllState() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        UserDto user3 = new UserDto(null, "asdf", "zxc@qwerty.com");
        mockMvc.perform(postRequest(user3));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStart(time.plusDays(8));
        bookingDto1.setEnd(time.plusDays(10));
        mockMvc.perform(postRequest(bookingDto1, 3L));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].start", is("2022-10-09T00:00:00")))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].start", is("2022-10-04T00:00:00")));
    }

    @Test
    public void getAllForOwnerWithFutureState() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        UserDto user3 = new UserDto(null, "asdf", "zxc@qwerty.com");
        mockMvc.perform(postRequest(user3));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStart(time.plusDays(8));
        bookingDto1.setEnd(time.plusDays(10));
        mockMvc.perform(postRequest(bookingDto1, 3L));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/owner")
                        .param("state", "FUTURE")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].start", is("2022-10-09T00:00:00")))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].start", is("2022-10-04T00:00:00")));
        ;
    }

    @Test
    public void getAllForOwnerWithWaitingState() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        UserDto user3 = new UserDto(null, "asdf", "zxc@qwerty.com");
        mockMvc.perform(postRequest(user3));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStart(time.plusDays(8));
        bookingDto1.setEnd(time.plusDays(10));
        mockMvc.perform(postRequest(bookingDto1, 3L));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/owner")
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].start", is("2022-10-09T00:00:00")))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].start", is("2022-10-04T00:00:00")));
        ;
    }

    @Test
    public void getAllForOwnerWithRejectedState() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        UserDto user3 = new UserDto(null, "asdf", "zxc@qwerty.com");
        mockMvc.perform(postRequest(user3));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStart(time.plusDays(8));
        bookingDto1.setEnd(time.plusDays(10));
        mockMvc.perform(postRequest(bookingDto1, 3L));

        mockMvc.perform(patchRequest(1L, "false", 1L));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/owner")
                        .param("state", "REJECTED")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].start", is("2022-10-04T00:00:00")));
    }

    @Test
    public void getAllForOwnerWithWrongState() throws Exception {
        UserDto user = new UserDto(null, "name", "mail@qwerty.com");
        mockMvc.perform(postRequest(user));

        UserDto user2 = new UserDto(null, "qwerty", "asd@qwerty.com");
        mockMvc.perform(postRequest(user2));

        UserDto user3 = new UserDto(null, "asdf", "zxc@qwerty.com");
        mockMvc.perform(postRequest(user3));

        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        mockMvc.perform(postRequest(item, 1L));

        LocalDateTime time = LocalDateTime.of(2022, 10, 1, 0, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time.plusDays(3));
        bookingDto.setEnd(time.plusDays(7));
        mockMvc.perform(postRequest(bookingDto, 2L));

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setItemId(1L);
        bookingDto1.setStart(time.plusDays(8));
        bookingDto1.setEnd(time.plusDays(10));
        mockMvc.perform(postRequest(bookingDto1, 3L));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/owner")
                        .param("state", "WRONG")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
    }

    private @NotNull MockHttpServletRequestBuilder postRequest(UserDto user) throws JsonProcessingException {
        return MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(user));
    }

    private @NotNull MockHttpServletRequestBuilder postRequest(ItemDto item,
                                                               Long ownerId) throws JsonProcessingException {
        return MockMvcRequestBuilders.post("/items")
                .header("X-Sharer-User-Id", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(item));
    }

    private @NotNull MockHttpServletRequestBuilder postRequest(BookingDto booking,
                                                               Long userId) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(url)
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(booking));
    }

    private MockHttpServletRequestBuilder patchRequest(Long bookingId, String approve, Long ownerId) {
        return MockMvcRequestBuilders
                .patch(url + "/" + bookingId)
                .param("approved", approve)
                .header("X-Sharer-User-Id", ownerId);
    }
}

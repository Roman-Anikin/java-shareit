package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql")
@Sql(scripts = "/delete-data.sql", executionPhase = AFTER_TEST_METHOD)
public class BookingIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository repository;

    @BeforeEach
    public void setUp() {
        UserDto owner = new UserDto(null, "owner", "mail@qwerty.com");
        UserDto booker = new UserDto(null, "booker", "asd@qwerty.com");
        ItemDto item = new ItemDto(null, "item", "desc", true, null);
        ItemDto item2 = new ItemDto(null, "item2", "desc2", true, null);
        BookingDto bookingDto = new BookingDto(null, getLTD(2), getLTD(3), null, 1L,
                null, null, null);
        BookingDto bookingDto2 = new BookingDto(null, getLTD(2), getLTD(3), null, 2L,
                null, null, null);
        userService.add(owner);
        userService.add(booker);
        itemService.add(1L, item);
        itemService.add(1L, item2);
        bookingService.add(2L, bookingDto);
        bookingService.add(2L, bookingDto2);
    }

    @Test
    public void addBooking() {
        BookingDto bookingDto = new BookingDto(null, getLTD(2), getLTD(3), null, 1L,
                null, null, null);
        bookingService.add(2L, bookingDto);

        Optional<Booking> foundBooking = repository.findById(3L);
        assertThat(foundBooking).isNotEmpty();
        assertThat(foundBooking.get().getId()).isEqualTo(3L);
        assertThat(foundBooking.get().getStart()).isEqualTo(bookingDto.getStart());
        assertThat(foundBooking.get().getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(foundBooking.get().getItem().getId()).isEqualTo(1L);
        assertThat(foundBooking.get().getBooker().getId()).isEqualTo(2L);
        assertThat(foundBooking.get().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    public void setApprove() {
        bookingService.makeApprove(1L, 1L, true);

        Optional<Booking> foundBooking = repository.findById(1L);
        assertThat(foundBooking.get().getId()).isEqualTo(1L);
        assertThat(foundBooking.get().getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    public void getById() {
        Optional<Booking> foundBooking = repository.findById(1L);
        assertThat(foundBooking.get().getId()).isEqualTo(1L);
        assertThat(foundBooking.get().getItem().getId()).isEqualTo(1L);
        assertThat(foundBooking.get().getBooker().getId()).isEqualTo(2L);
    }

    @Test
    public void getAllForUser() {
        List<Booking> bookings = repository.findByBookerId(2L, Pageable.unpaged());
        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(1L);
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(1L);
        assertThat(bookings.get(1).getId()).isEqualTo(2L);
        assertThat(bookings.get(1).getItem().getId()).isEqualTo(2L);
    }

    @Test
    public void getAllForOwner() {
        List<Booking> bookings = repository.findByItemOwnerId(1L, Pageable.unpaged());
        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(1L);
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(1L);
        assertThat(bookings.get(1).getId()).isEqualTo(2L);
        assertThat(bookings.get(1).getItem().getId()).isEqualTo(2L);
    }

    private LocalDateTime getLTD(int sec) {
        return LocalDateTime.now().plusSeconds(sec).truncatedTo(ChronoUnit.SECONDS);
    }
}

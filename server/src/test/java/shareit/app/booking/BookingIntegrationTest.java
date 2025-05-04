package shareit.app.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import shareit.app.booking.dto.BookingDto;
import shareit.app.item.ItemService;
import shareit.app.item.dto.ItemDto;
import shareit.app.user.UserService;
import shareit.app.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JdbcTemplate jdbc;

    private UserDto owner;
    private UserDto booker;
    private ItemDto item1;
    private ItemDto item2;
    private BookingDto booking1;
    private BookingDto booking2;

    @BeforeEach
    public void setUp() {
        cleanDatabase();

        owner = userService.add(new UserDto(null, "owner", "mail@qwerty.com"));
        booker = userService.add(new UserDto(null, "booker", "asd@qwerty.com"));
        item1 = itemService.add(owner.getId(),
                new ItemDto(null, "item 1", "desc 1", true, null));
        item2 = itemService.add(owner.getId(),
                new ItemDto(null, "item 2", "desc 2", true, null));
        booking1 = bookingService.add(booker.getId(),
                new BookingDto(null, getLTD(2), getLTD(3), null, item1.getId(),
                        null, null, null));
        booking2 = bookingService.add(booker.getId(),
                new BookingDto(null, getLTD(2), getLTD(3), null, item2.getId(),
                        null, null, null));
    }

    @Test
    public void addBooking() {
        BookingDto foundBooking = bookingService.getById(owner.getId(), item1.getId());

        assertThat(foundBooking.getId()).isEqualTo(booking1.getId());
        assertThat(foundBooking.getStart()).isEqualTo(booking1.getStart());
        assertThat(foundBooking.getEnd()).isEqualTo(booking1.getEnd());
        assertThat(foundBooking.getItem().getId()).isEqualTo(item1.getId());
        assertThat(foundBooking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(foundBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    public void setApprove() {
        bookingService.makeApprove(owner.getId(), booking1.getId(), true);

        BookingDto foundBooking = bookingService.getById(owner.getId(), item1.getId());
        assertThat(foundBooking.getId()).isEqualTo(booking1.getId());
        assertThat(foundBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    public void getById() {
        BookingDto foundBooking = bookingService.getById(owner.getId(), item1.getId());

        assertThat(foundBooking.getId()).isEqualTo(booking1.getId());
        assertThat(foundBooking.getItem().getId()).isEqualTo(item1.getId());
        assertThat(foundBooking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(foundBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    public void getAllForUser() {
        List<BookingDto> bookings = bookingService.getByUserAndState(booker.getId(), "ALL", 0, 5);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item1.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(1).getItem().getId()).isEqualTo(item2.getId());
    }

    @Test
    public void getAllForOwner() {
        List<BookingDto> bookings = bookingService.getByOwnerAndState(owner.getId(), "ALL", 0, 5);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item1.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(1).getItem().getId()).isEqualTo(item2.getId());
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

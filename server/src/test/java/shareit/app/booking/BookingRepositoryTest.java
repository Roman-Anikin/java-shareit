package shareit.app.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import shareit.app.item.Item;
import shareit.app.user.User;
import shareit.app.util.OffsetPageRequest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository repository;

    @Autowired
    private TestEntityManager manager;

    @Autowired
    private JdbcTemplate jdbc;

    private User owner;
    private User booker;
    private Item item1;
    private Item item2;
    private Item item3;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;

    @BeforeEach
    public void setUp() {
        cleanDatabase();

        owner = manager.persist(new User(null, "owner", "qwe@mail.com"));
        booker = manager.persist(new User(null, "booker", "asd@mail.com"));
        item1 = manager.persist(new Item(null, "item 1", "desc 1", true, owner, null));
        item2 = manager.persist(new Item(null, "item 2", "desc 2", true, owner, null));
        item3 = manager.persist(new Item(null, "item 3", "desc 3", true, owner, null));
        booking1 = manager.persist(new Booking(null, getLTD(2), getLTD(3), item1, booker,
                BookingStatus.WAITING));
        booking2 = manager.persist(new Booking(null, getLTD(2), getLTD(4), item2, booker,
                BookingStatus.APPROVED));
        booking3 = manager.persist(new Booking(null, getLTD(3), getLTD(5), item3, booker,
                BookingStatus.REJECTED));
    }

    @Test
    public void getByUserAndStateAll() {
        List<Booking> bookings = repository.findByBookerId(booker.getId(), Pageable.unpaged());

        assertThat(bookings).hasSize(3);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item1.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(1).getItem().getId()).isEqualTo(item2.getId());
        assertThat(bookings.get(2).getId()).isEqualTo(booking3.getId());
        assertThat(bookings.get(2).getItem().getId()).isEqualTo(item3.getId());
    }

    @Test
    public void getByUserAndStateAllWithPagination() {
        List<Booking> bookings = repository.findByBookerId(booker.getId(),
                new OffsetPageRequest(1, 1, Sort.unsorted()));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item2.getId());
    }

    @Test
    public void getByUserAndStateCurrent() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        List<Booking> bookings = repository.findByBookerIdAndStartBeforeAndEndAfter(booker.getId(), LocalDateTime.now(),
                LocalDateTime.now(), Pageable.unpaged());

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item1.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(1).getItem().getId()).isEqualTo(item2.getId());
    }

    @Test
    public void getByUserAndStateCurrentWithPagination() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        List<Booking> bookings = repository.findByBookerIdAndStartBeforeAndEndAfter(booker.getId(), LocalDateTime.now(),
                LocalDateTime.now(), new OffsetPageRequest(1, 1, Sort.unsorted()));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item2.getId());
    }

    @Test
    public void getByUserAndStatePast() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        List<Booking> bookings = repository.findByBookerIdAndEndBefore(booker.getId(), LocalDateTime.now(),
                Pageable.unpaged());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item1.getId());
    }

    @Test
    public void getByUserAndStatePastWithPagination() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        List<Booking> bookings = repository.findByBookerIdAndEndBefore(booker.getId(), LocalDateTime.now(),
                new OffsetPageRequest(1, 1, Sort.unsorted()));

        assertThat(bookings).hasSize(0);
    }

    @Test
    public void getByUserAndStateFuture() {
        List<Booking> bookings = repository.findByBookerIdAndStartAfter(booker.getId(), LocalDateTime.now(),
                Pageable.unpaged());

        assertThat(bookings).hasSize(3);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item1.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(1).getItem().getId()).isEqualTo(item2.getId());
        assertThat(bookings.get(2).getId()).isEqualTo(booking3.getId());
        assertThat(bookings.get(2).getItem().getId()).isEqualTo(item3.getId());
    }

    @Test
    public void getByUserAndStateFutureWithPagination() {
        List<Booking> bookings = repository.findByBookerIdAndStartAfter(booker.getId(), LocalDateTime.now(),
                new OffsetPageRequest(2, 1, Sort.unsorted()));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking3.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item3.getId());
    }

    @Test
    public void getByUserAndStateWaiting() {
        List<Booking> bookings = repository.findByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING,
                Pageable.unpaged());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item1.getId());

    }

    @Test
    public void getByUserAndStateWaitingWithPagination() {
        List<Booking> bookings = repository.findByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING,
                new OffsetPageRequest(1, 1, Sort.unsorted()));

        assertThat(bookings).hasSize(0);
    }

    @Test
    public void getByUserAndStateRejected() {
        List<Booking> bookings = repository.findByBookerIdAndStatus(booker.getId(), BookingStatus.REJECTED,
                Pageable.unpaged());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking3.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item3.getId());
    }

    @Test
    public void getByUserAndStateRejectedWithPagination() {
        List<Booking> bookings = repository.findByBookerIdAndStatus(booker.getId(), BookingStatus.REJECTED,
                new OffsetPageRequest(0, 1, Sort.unsorted()));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking3.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item3.getId());
    }

    @Test
    public void getBookingForComment() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        Optional<Booking> booking = repository.findByItemIdAndBookerIdAndEndBefore(item1.getId(), booker.getId(),
                LocalDateTime.now());

        assertThat(booking.get().getId()).isEqualTo(booking1.getId());
        assertThat(booking.get().getItem().getId()).isEqualTo(item1.getId());
    }

    @Test
    public void getByOwnerAndStateAll() {
        List<Booking> bookings = repository.findByItemOwnerId(owner.getId(), Pageable.unpaged());

        assertThat(bookings).hasSize(3);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item1.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(1).getItem().getId()).isEqualTo(item2.getId());
        assertThat(bookings.get(2).getId()).isEqualTo(booking3.getId());
        assertThat(bookings.get(2).getItem().getId()).isEqualTo(item3.getId());
    }

    @Test
    public void getByOwnerAndStateAllWithPagination() {
        List<Booking> bookings = repository.findByItemOwnerId(owner.getId(),
                new OffsetPageRequest(1, 1, Sort.unsorted()));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item2.getId());
    }

    @Test
    public void getByOwnerAndStateCurrent() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        List<Booking> bookings = repository.findByItemOwnerIdAndStartBeforeAndEndAfter(owner.getId(),
                LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged());

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item1.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(1).getItem().getId()).isEqualTo(item2.getId());
    }

    @Test
    public void getByOwnerAndStateCurrentWithPagination() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        List<Booking> bookings = repository.findByItemOwnerIdAndStartBeforeAndEndAfter(owner.getId(),
                LocalDateTime.now(), LocalDateTime.now(), new OffsetPageRequest(1, 1, Sort.unsorted()));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item2.getId());
    }

    @Test
    public void getByOwnerAndStatePast() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        List<Booking> bookings = repository.findByItemOwnerIdAndEndBefore(owner.getId(), LocalDateTime.now(),
                Pageable.unpaged());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item1.getId());
    }

    @Test
    public void getByOwnerAndStatePastWithPagination() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        List<Booking> bookings = repository.findByItemOwnerIdAndEndBefore(owner.getId(), LocalDateTime.now(),
                new OffsetPageRequest(1, 1, Sort.unsorted()));

        assertThat(bookings).hasSize(0);
    }

    @Test
    public void getByOwnerAndStateFuture() {
        List<Booking> bookings = repository.findByItemOwnerIdAndStartAfter(owner.getId(), LocalDateTime.now(),
                Pageable.unpaged());

        assertThat(bookings).hasSize(3);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item1.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(1).getItem().getId()).isEqualTo(item2.getId());
        assertThat(bookings.get(2).getId()).isEqualTo(booking3.getId());
        assertThat(bookings.get(2).getItem().getId()).isEqualTo(item3.getId());
    }

    @Test
    public void getByOwnerAndStateFutureWithPagination() {
        List<Booking> bookings = repository.findByItemOwnerIdAndStartAfter(owner.getId(), LocalDateTime.now(),
                new OffsetPageRequest(2, 1, Sort.unsorted()));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking3.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item3.getId());
    }

    @Test
    public void getByOwnerAndStateWaiting() {
        List<Booking> bookings = repository.findByItemOwnerIdAndStatus(owner.getId(), BookingStatus.WAITING,
                Pageable.unpaged());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item1.getId());
    }

    @Test
    public void getByOwnerAndStateWaitingWithPagination() {
        List<Booking> bookings = repository.findByItemOwnerIdAndStatus(owner.getId(), BookingStatus.WAITING,
                new OffsetPageRequest(1, 1, Sort.unsorted()));

        assertThat(bookings).hasSize(0);
    }

    @Test
    public void getByOwnerAndStateRejected() {
        List<Booking> bookings = repository.findByItemOwnerIdAndStatus(owner.getId(), BookingStatus.REJECTED,
                Pageable.unpaged());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking3.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item3.getId());
    }

    @Test
    public void getByOwnerAndStateRejectedWithPagination() {
        List<Booking> bookings = repository.findByItemOwnerIdAndStatus(owner.getId(), BookingStatus.REJECTED,
                new OffsetPageRequest(0, 1, Sort.unsorted()));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking3.getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(item3.getId());
    }

    @Test
    public void getLastBooking() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        Booking booking = repository.findFirstByItemIdAndEndBefore(item1.getId(), LocalDateTime.now(), Sort.unsorted());

        assertThat(booking.getId()).isEqualTo(booking1.getId());
        assertThat(booking.getItem().getId()).isEqualTo(item1.getId());
    }

    @Test
    public void getNextBooking() {
        Booking booking = repository.findFirstByItemIdAndStartAfter(item2.getId(), LocalDateTime.now(), Sort.unsorted());

        assertThat(booking.getId()).isEqualTo(booking2.getId());
        assertThat(booking.getItem().getId()).isEqualTo(item2.getId());
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

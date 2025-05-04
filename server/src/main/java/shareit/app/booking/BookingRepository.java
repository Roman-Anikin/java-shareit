package shareit.app.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    String request = "SELECT booking_id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "FROM items i " +
            "LEFT JOIN bookings b ON i.item_id = b.item_id " +
            "WHERE i.owner_id = ?1 ";

    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId,
                                                          LocalDateTime before,
                                                          LocalDateTime after,
                                                          Pageable pageable);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime time, Pageable pageable);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime time, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    @Query(name = "SELECT *" +
            "FROM bookings b " +
            "WHERE b.item_id = ?1 AND b.booker_id = ?2 AND b.end_date < ?3 " +
            "AND (b.status LIKE 'APPROVED' OR b.status LIKE 'WAITING') " +
            "LIMIT 1", nativeQuery = true)
    Optional<Booking> findByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime time);

    @Query(name = request + " AND (b.status LIKE 'WAITING' OR b.status LIKE 'APPROVED') ", nativeQuery = true)
    List<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId,
                                                             LocalDateTime before,
                                                             LocalDateTime after,
                                                             Pageable pageable);

    @Query(name = request + "AND (b.status LIKE 'WAITING' OR b.status LIKE 'APPROVED') " +
            "AND b.end_date < ?2 ", nativeQuery = true)
    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime time, Pageable pageable);

    @Query(name = request + "AND (b.status LIKE 'WAITING' OR b.status LIKE 'APPROVED') " +
            "AND b.start_date > ?2 ", nativeQuery = true)
    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime time, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    Booking findFirstByItemIdAndEndBefore(Long itemId, LocalDateTime time, Sort sort);

    Booking findFirstByItemIdAndStartAfter(Long itemId, LocalDateTime time, Sort sort);
}
package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                              LocalDateTime before,
                                                              LocalDateTime after,
                                                              Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStatusIs(Long bookerId, BookingStatus status, Sort sort);

    @Query(name = "SELECT *" +
            "FROM bookings b " +
            "WHERE b.item_id = ?1 AND b.booker_id = ?2 AND b.end_date < ?3 " +
            "AND (b.status LIKE 'APPROVED' OR b.status LIKE 'WAITING') " +
            "LIMIT 1", nativeQuery = true)
    Booking findByItemIdAndBookerIdAndEndIsBefore(Long itemId, Long userId, LocalDateTime time);
}

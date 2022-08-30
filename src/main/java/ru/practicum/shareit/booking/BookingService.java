package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingDto add(Long userId, BookingDto bookingDto);

    BookingDto makeApprove(Long ownerId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getByUserAndState(Long bookerId, String state);

    List<BookingDto> getByOwnerAndState(Long ownerId, String state);

    BookingDto getLastBooking(Long ownerId, Long itemId);

    BookingDto getNextBooking(Long ownerId, Long itemId);

    Booking getByItemId(Long itemId, Long userId, LocalDateTime time);
}

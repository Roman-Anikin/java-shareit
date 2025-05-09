package shareit.app.booking;

import shareit.app.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingDto add(Long userId, BookingDto bookingDto);

    BookingDto makeApprove(Long ownerId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getByUserAndState(Long bookerId, String state, Integer from, Integer size);

    List<BookingDto> getByOwnerAndState(Long ownerId, String state, Integer from, Integer size);

    BookingDto getLastBooking(Long itemId);

    BookingDto getNextBooking(Long itemId);

    Booking getByItemId(Long itemId, Long userId, LocalDateTime time);
}

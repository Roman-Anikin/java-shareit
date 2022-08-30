package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepositoryCustom {

    List<Booking> getByOwnerAndAllState(Long ownerId);

    List<Booking> getByOwnerAndCurrentState(Long bookerId, LocalDateTime time);

    List<Booking> getByOwnerAndPastState(Long bookerId, LocalDateTime time);

    List<Booking> getByOwnerAndFutureState(Long bookerId, LocalDateTime time);

    List<Booking> getByOwnerAndWaitingOrRejectedState(Long bookerId, BookingStatus status);

    Booking getLastBooking(Long ownerId, Long itemId, LocalDateTime time);

    Booking getNextBooking(Long ownerId, Long itemId, LocalDateTime time);
}

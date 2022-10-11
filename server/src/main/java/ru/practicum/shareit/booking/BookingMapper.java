package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public Booking convertFromDto(BookingDto bookingDto) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                new Item(),
                new User(),
                BookingStatus.WAITING);
    }

    public BookingDto convertToDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getItem().getId(),
                booking.getBooker(),
                booking.getBooker().getId(),
                booking.getStatus());
    }

    public List<BookingDto> convertToDto(List<Booking> bookings) {
        return bookings
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}

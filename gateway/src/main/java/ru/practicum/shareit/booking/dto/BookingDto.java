package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingDto {

    private Long id;

    @NotNull(message = "Начало аренды не может быть пустым")
    @FutureOrPresent(message = "Начало аренды не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Конец аренды не может быть пустым")
    @FutureOrPresent(message = "Конец аренды не может быть в прошлом")
    private LocalDateTime end;

    private Item item;

    private Long itemId;

    private User booker;

    private Long bookerId;

    private BookingStatus status;
}

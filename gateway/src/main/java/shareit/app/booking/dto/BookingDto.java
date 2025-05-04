package shareit.app.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import shareit.app.booking.BookingStatus;
import shareit.app.item.Item;
import shareit.app.user.User;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;

    @NotNull(message = "Конец аренды не может быть пустым")
    @FutureOrPresent(message = "Конец аренды не может быть в прошлом")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;

    private Item item;

    @NotNull(message = "Id предмета не может быть пустым")
    private Long itemId;

    private User booker;

    private Long bookerId;

    private BookingStatus status;

}

package shareit.app.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;

    private Item item;
    private Long itemId;
    private User booker;
    private Long bookerId;
    private BookingStatus status;

}

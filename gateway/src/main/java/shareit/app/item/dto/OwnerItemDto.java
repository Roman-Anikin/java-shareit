package shareit.app.item.dto;

import lombok.*;
import shareit.app.booking.dto.BookingDto;
import shareit.app.requests.ItemRequest;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OwnerItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
    private ItemRequest itemRequest;

}

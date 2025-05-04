package shareit.app.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import shareit.app.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemRequestDto {

    private Long id;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    private List<ItemDto> items;

}
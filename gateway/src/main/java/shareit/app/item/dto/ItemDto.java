package shareit.app.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @NotEmpty(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    @NotEmpty(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Статус доступа не может быть пустым")
    private Boolean available;

    private Long requestId;

}
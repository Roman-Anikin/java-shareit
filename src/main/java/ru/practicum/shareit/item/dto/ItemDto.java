package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.requests.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDto {

    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    @NotEmpty(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    @NotEmpty(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Статус доступа не может быть пустым")
    private Boolean available;

    private Long ownerId;
    private ItemRequest itemRequest;
}

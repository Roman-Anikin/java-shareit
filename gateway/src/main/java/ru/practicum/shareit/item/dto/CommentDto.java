package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentDto {

    private Long id;

    @NotBlank(message = "Комментарий не может быть пустым")
    @NotEmpty(message = "Комментарий не может быть пустым")
    private String text;
    private String authorName;
    private LocalDateTime created;
}

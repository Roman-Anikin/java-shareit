package shareit.app.requests;

import lombok.*;
import shareit.app.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemRequest {

    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
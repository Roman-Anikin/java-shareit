package shareit.app.item;

import lombok.*;
import shareit.app.requests.ItemRequest;
import shareit.app.user.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Item {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}

package shareit.app.item;

import jakarta.persistence.*;
import lombok.*;
import shareit.app.requests.ItemRequest;
import shareit.app.user.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;
    private String name;
    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}

package shareit.app.requests;

import jakarta.persistence.*;
import lombok.*;
import shareit.app.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    private String description;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "requester_id")
    private User requester;

    @Column(name = "create_date")
    private LocalDateTime created;
}
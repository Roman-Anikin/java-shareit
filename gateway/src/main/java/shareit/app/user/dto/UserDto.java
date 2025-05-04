package shareit.app.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {

    private Long id;
    private String name;

    @Email(message = "Неверный формат электронной почты")
    private String email;

}

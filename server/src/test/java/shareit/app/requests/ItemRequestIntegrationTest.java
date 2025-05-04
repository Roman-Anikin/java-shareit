package shareit.app.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import shareit.app.requests.dto.ItemRequestDto;
import shareit.app.user.UserService;
import shareit.app.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRequestIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private JdbcTemplate jdbc;

    private UserDto user1;
    private UserDto user2;
    private ItemRequestDto request1;
    private ItemRequestDto request2;
    private ItemRequestDto request3;

    @BeforeEach
    public void setUp() {
        cleanDatabase();

        user1 = userService.add(new UserDto(null, "requester 1", "qwe@mail.com"));
        user2 = userService.add(new UserDto(null, "requester 2", "asd@mail.com"));
        request1 = requestService.add(user1.getId(),
                new ItemRequestDto(null, "text 1", null, null));
        request2 = requestService.add(user2.getId(),
                new ItemRequestDto(null, "text 2", null, null));
        request3 = requestService.add(user2.getId(),
                new ItemRequestDto(null, "text 3", null, null));
    }

    @Test
    public void addRequest() {
        ItemRequestDto request = requestService.add(user1.getId(),
                new ItemRequestDto(null, "text 4", null, null));

        ItemRequestDto foundRequest = requestService.getById(user1.getId(), request.getId());
        assertThat(foundRequest.getId()).isEqualTo(request.getId());
        assertThat(foundRequest.getDescription()).isEqualTo(request.getDescription());
        assertThat(foundRequest.getCreated()).isNotNull();
        assertThat(foundRequest.getItems()).isNotNull();
    }

    @Test
    public void getAllByRequester() {
        List<ItemRequestDto> requests = requestService.getAllByRequester(user1.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getId()).isEqualTo(request1.getId());
        assertThat(requests.get(0).getDescription()).isEqualTo(request1.getDescription());
    }

    @Test
    public void getAllExceptRequester() {
        List<ItemRequestDto> requests = requestService.getAllExceptRequester(user1.getId(), 0, 5);

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getId()).isEqualTo(request3.getId());
        assertThat(requests.get(0).getDescription()).isEqualTo(request3.getDescription());
        assertThat(requests.get(1).getId()).isEqualTo(request2.getId());
        assertThat(requests.get(1).getDescription()).isEqualTo(request2.getDescription());
    }

    @Test
    public void getById() {
        ItemRequestDto request = requestService.getById(user2.getId(), request2.getId());

        assertThat(request.getId()).isEqualTo(request2.getId());
        assertThat(request.getDescription()).isEqualTo(request2.getDescription());
        assertThat(request.getCreated()).isNotNull();
        assertThat(request.getItems()).isNotNull();
    }

    void cleanDatabase() {
        jdbc.execute("DELETE FROM bookings");
        jdbc.execute("DELETE FROM comments");
        jdbc.execute("DELETE FROM items");
        jdbc.execute("DELETE FROM requests");
        jdbc.execute("DELETE FROM users");
    }
}

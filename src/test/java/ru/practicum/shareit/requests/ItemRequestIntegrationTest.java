package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql")
@Sql(scripts = "/delete-data.sql", executionPhase = AFTER_TEST_METHOD)
public class ItemRequestIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private ItemRequestRepository repository;

    @BeforeEach
    public void setUp() {
        UserDto requester = new UserDto(null, "requester", "qwe@mail.com");
        UserDto requester2 = new UserDto(null, "requester2", "asd@mail.com");
        ItemRequestDto request = new ItemRequestDto(null, "text", null, null);
        ItemRequestDto request2 = new ItemRequestDto(null, "text2", null, null);
        ItemRequestDto request3 = new ItemRequestDto(null, "text3", null, null);
        userService.add(requester);
        userService.add(requester2);
        requestService.add(1L, request);
        requestService.add(2L, request2);
        requestService.add(2L, request3);
    }

    @Test
    public void addRequest() {
        ItemRequestDto request = new ItemRequestDto(null, "text4", null, null);
        requestService.add(1L, request);

        Optional<ItemRequest> foundRequest = repository.findById(4L);
        assertThat(foundRequest.get().getId()).isEqualTo(4L);
        assertThat(foundRequest.get().getDescription()).isEqualTo(request.getDescription());
        assertThat(foundRequest.get().getRequester().getId()).isEqualTo(1L);
        assertThat(foundRequest.get().getCreated()).isNotNull();
    }

    @Test
    public void getAllByRequester() {
        List<ItemRequest> requests = repository.findAllByRequesterId(2L, Sort.unsorted());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getId()).isEqualTo(2L);
        assertThat(requests.get(0).getDescription()).isEqualTo("text2");
        assertThat(requests.get(1).getId()).isEqualTo(3L);
        assertThat(requests.get(1).getDescription()).isEqualTo("text3");
    }

    @Test
    public void getAllExceptRequester() {
        List<ItemRequest> requests = repository.findAllByRequesterIdNot(1L, Pageable.unpaged());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getId()).isEqualTo(2L);
        assertThat(requests.get(0).getDescription()).isEqualTo("text2");
        assertThat(requests.get(1).getId()).isEqualTo(3L);
        assertThat(requests.get(1).getDescription()).isEqualTo("text3");
    }

    @Test
    public void getById() {
        Optional<ItemRequest> request = repository.findById(3L);

        assertThat(request.get().getRequester().getId()).isEqualTo(2L);
        assertThat(request.get().getDescription()).isEqualTo("text3");
    }
}

package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(UserClient.class)
public class UserClientTest {

    private final String url = "http://localhost:9090/users";
    @Autowired
    private UserClient client;
    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void add() throws JsonProcessingException {
        makeRequest("", new UserDto());
        ResponseEntity<UserDto> response = client.add(new UserDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update() throws JsonProcessingException {
        makeRequest("/1", new UserDto());
        ResponseEntity<UserDto> response = client.update(new UserDto(), 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete() throws JsonProcessingException {
        makeRequest("/1", new UserDto());
        client.delete(1L);
    }

    @Test
    public void getById() throws JsonProcessingException {
        makeRequest("/1", new UserDto());
        ResponseEntity<UserDto> response = client.getById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getAll() throws JsonProcessingException {
        makeRequest("", List.of(new UserDto()));
        ResponseEntity<List<UserDto>> response = client.getAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    private <T> void makeRequest(String path, T dto) throws JsonProcessingException {
        String json = mapper.writeValueAsString(dto);

        server.expect(requestTo(url + path))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    }
}

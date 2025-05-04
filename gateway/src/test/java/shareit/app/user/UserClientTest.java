package shareit.app.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.test.StepVerifier;
import shareit.app.user.dto.UserDto;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserClientTest {

    private UserClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockWebServer server;

    @BeforeEach
    void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        client = new UserClient(server.url("/").toString());
    }

    @AfterEach
    void shutdown() throws IOException {
        server.shutdown();
    }

    @Test
    void add() throws JsonProcessingException {
        UserDto dto = new UserDto(1L, "name", "email@mail.com");
        enqueue(dto);

        StepVerifier.create(client.add(new UserDto()))
                .assertNext(result -> {
                    assertEquals(dto.getId(), result.getId());
                    assertEquals(dto.getName(), result.getName());
                    assertEquals(dto.getEmail(), result.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void update() throws JsonProcessingException {
        UserDto dto = new UserDto(1L, "name", "email@mail.com");
        enqueue(dto);

        StepVerifier.create(client.update(new UserDto(), 1L))
                .assertNext(result -> {
                    assertEquals(dto.getId(), result.getId());
                    assertEquals(dto.getName(), result.getName());
                    assertEquals(dto.getEmail(), result.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void delete() throws JsonProcessingException {
        UserDto dto = new UserDto(1L, "name", "email@mail.com");
        enqueue(dto);

        StepVerifier.create(client.delete(1L))
                .verifyComplete();
    }

    @Test
    void getById() throws JsonProcessingException {
        UserDto dto = new UserDto(1L, "name", "email@mail.com");
        enqueue(dto);

        StepVerifier.create(client.getById(1L))
                .assertNext(result -> {
                    assertEquals(dto.getId(), result.getId());
                    assertEquals(dto.getName(), result.getName());
                    assertEquals(dto.getEmail(), result.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void getAll() throws JsonProcessingException {
        UserDto dto1 = new UserDto(1L, "name 1", "email1@mail.com");
        UserDto dto2 = new UserDto(2L, "name 2", "email2@mail.com");

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(List.of(dto1, dto2)))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        StepVerifier.create(client.getAll())
                .assertNext(result -> {
                    assertEquals(result.size(), 2);
                    assertEquals(result.get(0).getId(), dto1.getId());
                    assertEquals(result.get(1).getId(), dto2.getId());
                })
                .verifyComplete();
    }

    private void enqueue(UserDto dto) throws JsonProcessingException {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(dto))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }
}

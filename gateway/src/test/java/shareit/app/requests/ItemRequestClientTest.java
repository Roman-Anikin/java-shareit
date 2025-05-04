package shareit.app.requests;

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
import shareit.app.requests.dto.ItemRequestDto;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestClientTest {

    private ItemRequestClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockWebServer server;

    @BeforeEach
    void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        client = new ItemRequestClient(server.url("/").toString());
    }

    @AfterEach
    void shutdown() throws IOException {
        server.shutdown();
    }

    @Test
    public void add() throws JsonProcessingException {
        ItemRequestDto dto = new ItemRequestDto(1L, "desk", null, null);
        enqueue(dto);

        StepVerifier.create(client.add(1L, new ItemRequestDto()))
                .assertNext(result -> {
                    assertEquals(dto.getId(), result.getId());
                    assertEquals(dto.getDescription(), result.getDescription());
                })
                .verifyComplete();
    }

    @Test
    public void getAllByRequester() throws JsonProcessingException {
        ItemRequestDto dto = new ItemRequestDto(1L, "desk", null, null);
        enqueue(dto);

        StepVerifier.create(client.getAllByRequester(1L))
                .assertNext(result -> {
                    assertEquals(1, result.size());
                    assertEquals(dto.getId(), result.get(0).getId());
                    assertEquals(dto.getDescription(), result.get(0).getDescription());
                })
                .verifyComplete();
    }

    @Test
    public void getAllExceptRequester() throws JsonProcessingException {
        ItemRequestDto dto = new ItemRequestDto(1L, "desk", null, null);
        enqueue(dto);

        StepVerifier.create(client.getAllExceptRequester(1L, 0, 5))
                .assertNext(result -> {
                    assertEquals(1, result.size());
                    assertEquals(dto.getId(), result.get(0).getId());
                    assertEquals(dto.getDescription(), result.get(0).getDescription());
                })
                .verifyComplete();
    }

    @Test
    public void getById() throws JsonProcessingException {
        ItemRequestDto dto = new ItemRequestDto(1L, "desk", null, null);
        enqueue(dto);

        StepVerifier.create(client.getById(1L, 1L))
                .assertNext(result -> {
                    assertEquals(dto.getId(), result.getId());
                    assertEquals(dto.getDescription(), result.getDescription());
                })
                .verifyComplete();
    }

    private void enqueue(ItemRequestDto dto) throws JsonProcessingException {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(dto))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }
}

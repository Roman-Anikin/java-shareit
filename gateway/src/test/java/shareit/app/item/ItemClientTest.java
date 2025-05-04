package shareit.app.item;

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
import shareit.app.item.dto.CommentDto;
import shareit.app.item.dto.ItemDto;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemClientTest {

    private ItemClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockWebServer server;

    @BeforeEach
    void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        client = new ItemClient(server.url("/").toString());
    }

    @AfterEach
    void shutdown() throws IOException {
        server.shutdown();
    }

    @Test
    public void add() throws JsonProcessingException {
        ItemDto dto = new ItemDto(1L, "item", "description", true, null);
        enqueue(dto);

        StepVerifier.create(client.add(1L, new ItemDto()))
                .assertNext(result -> {
                    assertEquals(dto.getId(), result.getId());
                    assertEquals(dto.getName(), result.getName());
                    assertEquals(dto.getDescription(), result.getDescription());
                })
                .verifyComplete();
    }

    @Test
    public void update() throws JsonProcessingException {
        ItemDto dto = new ItemDto(1L, "item", "description", true, null);
        enqueue(dto);

        StepVerifier.create(client.update(1L, 1L, new ItemDto()))
                .assertNext(result -> {
                    assertEquals(dto.getId(), result.getId());
                    assertEquals(dto.getName(), result.getName());
                    assertEquals(dto.getDescription(), result.getDescription());
                })
                .verifyComplete();
    }

    @Test
    public void getById() throws JsonProcessingException {
        ItemDto dto = new ItemDto(1L, "item", "description", true, null);
        enqueue(dto);

        StepVerifier.create(client.getById(1L, 1L))
                .assertNext(result -> {
                    assertEquals(dto.getId(), result.getId());
                    assertEquals(dto.getName(), result.getName());
                    assertEquals(dto.getDescription(), result.getDescription());
                })
                .verifyComplete();
    }

    @Test
    public void getByOwner() throws JsonProcessingException {
        ItemDto dto1 = new ItemDto(1L, "item 1", "description 1", true, null);
        ItemDto dto2 = new ItemDto(2L, "item 2", "description 2", true, null);

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(List.of(dto1, dto2)))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        StepVerifier.create(client.getByOwner(1L, 0, 5))
                .assertNext(result -> {
                    assertEquals(result.size(), 2);
                    assertEquals(result.get(0).getId(), dto1.getId());
                    assertEquals(result.get(1).getId(), dto2.getId());
                })
                .verifyComplete();
    }

    @Test
    public void searchByText() throws JsonProcessingException {
        ItemDto dto1 = new ItemDto(1L, "item 1", "description 1", true, null);
        ItemDto dto2 = new ItemDto(2L, "item 2", "description 2", true, null);

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(List.of(dto1, dto2)))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        StepVerifier.create(client.searchByText(1L, "text", 0, 5))
                .assertNext(result -> {
                    assertEquals(result.size(), 2);
                    assertEquals(result.get(0).getId(), dto1.getId());
                    assertEquals(result.get(1).getId(), dto2.getId());
                })
                .verifyComplete();
    }

    @Test
    public void addComment() throws JsonProcessingException {
        CommentDto dto = new CommentDto(1L, "comment", "user", null);

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(dto))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        StepVerifier.create(client.addComment(1L, 1L, dto))
                .assertNext(result -> {
                    assertEquals(result.getId(), dto.getId());
                    assertEquals(result.getText(), dto.getText());
                    assertEquals(result.getAuthorName(), dto.getAuthorName());
                })
                .verifyComplete();
    }

    private void enqueue(ItemDto dto) throws JsonProcessingException {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(dto))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }
}

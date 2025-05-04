package shareit.app.booking;

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
import shareit.app.booking.dto.BookingDto;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingClientTest {

    private BookingClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockWebServer server;

    @BeforeEach
    void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        client = new BookingClient(server.url("/").toString());
    }

    @AfterEach
    void shutdown() throws IOException {
        server.shutdown();
    }

    @Test
    void add() throws JsonProcessingException {
        BookingDto dto = new BookingDto(1L, null, null, null, 1L, null, 1L,
                BookingStatus.WAITING);
        enqueue(dto);

        StepVerifier.create(client.add(1L, new BookingDto()))
                .assertNext(result -> {
                    assertEquals(dto.getId(), result.getId());
                    assertEquals(dto.getStatus(), result.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void makeApprove() throws JsonProcessingException {
        BookingDto dto = new BookingDto(1L, null, null, null, 1L, null, 1L,
                BookingStatus.WAITING);
        enqueue(dto);

        StepVerifier.create(client.makeApprove(1L, 1L, true))
                .assertNext(result -> {
                    assertEquals(dto.getId(), result.getId());
                    assertEquals(dto.getStatus(), result.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void getById() throws JsonProcessingException {
        BookingDto dto = new BookingDto(1L, null, null, null, 1L, null, 1L,
                BookingStatus.WAITING);
        enqueue(dto);

        StepVerifier.create(client.getById(1L, 1L))
                .assertNext(result -> {
                    assertEquals(dto.getId(), result.getId());
                    assertEquals(dto.getStatus(), result.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void getByUserAndState() throws JsonProcessingException {
        BookingDto dto1 = new BookingDto(1L, null, null, null, 1L, null, 1L,
                BookingStatus.WAITING);
        BookingDto dto2 = new BookingDto(2L, null, null, null, 1L, null, 1L,
                BookingStatus.REJECTED);

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(List.of(dto1, dto2)))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        StepVerifier.create(client.getByUserAndState(1L, "ALL", 0, 5))
                .assertNext(result -> {
                    assertEquals(2, result.size());
                    assertEquals(dto1.getId(), result.get(0).getId());
                    assertEquals(dto1.getStatus(), result.get(0).getStatus());
                    assertEquals(dto2.getId(), result.get(1).getId());
                    assertEquals(dto2.getStatus(), result.get(1).getStatus());
                })
                .verifyComplete();
    }

    @Test
    void getByOwnerAndState() throws JsonProcessingException {
        BookingDto dto1 = new BookingDto(1L, null, null, null, 1L, null, 1L,
                BookingStatus.WAITING);
        BookingDto dto2 = new BookingDto(2L, null, null, null, 1L, null, 1L,
                BookingStatus.REJECTED);

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(List.of(dto1, dto2)))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        StepVerifier.create(client.getByOwnerAndState(1L, "ALL", 0, 5))
                .assertNext(result -> {
                    assertEquals(2, result.size());
                    assertEquals(dto1.getId(), result.get(0).getId());
                    assertEquals(dto1.getStatus(), result.get(0).getStatus());
                    assertEquals(dto2.getId(), result.get(1).getId());
                    assertEquals(dto2.getStatus(), result.get(1).getStatus());
                })
                .verifyComplete();
    }

    private void enqueue(BookingDto dto) throws JsonProcessingException {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(dto))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }
}

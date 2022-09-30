package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(BookingClient.class)
public class BookingClientTest {

    private final String url = "http://localhost:9090/bookings";
    @Autowired
    private BookingClient client;
    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void add() throws JsonProcessingException {
        makeRequest("", new BookingDto());
        ResponseEntity<BookingDto> response = client.add(1L, new BookingDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void makeApprove() throws JsonProcessingException {
        makeRequest("/1?approved=true", new BookingDto());
        ResponseEntity<BookingDto> response = client.makeApprove(1L, 1L, true);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getById() throws JsonProcessingException {
        makeRequest("/1", new BookingDto());
        ResponseEntity<BookingDto> response = client.getById(1L, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getByUserAndState() throws JsonProcessingException {
        makeRequest("?state=ALL&from=0&size=20", List.of(new BookingDto()));
        ResponseEntity<List<BookingDto>> response = client.getByUserAndState(1L, "ALL", 0, 20);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    public void getByOwnerAndState() throws JsonProcessingException {
        makeRequest("/owner?state=ALL&from=0&size=20", List.of(new BookingDto()));
        ResponseEntity<List<BookingDto>> response = client.getByOwnerAndState(1L, "ALL", 0, 20);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    private <T> void makeRequest(String path, T dto) throws JsonProcessingException {
        String json = mapper.writeValueAsString(dto);

        server.expect(requestTo(url + path))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    }
}

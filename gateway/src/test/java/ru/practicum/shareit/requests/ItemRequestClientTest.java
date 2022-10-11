package ru.practicum.shareit.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ItemRequestClient.class)
public class ItemRequestClientTest {

    private final String url = "http://localhost:9090/requests";
    @Autowired
    private ItemRequestClient client;
    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void add() throws JsonProcessingException {
        makeRequest("", new ItemRequestDto());
        ResponseEntity<ItemRequestDto> response = client.add(1L, new ItemRequestDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getAllByRequester() throws JsonProcessingException {
        makeRequest("", List.of(new ItemRequestDto()));
        ResponseEntity<List<ItemRequestDto>> response = client.getAllByRequester(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    public void getAllExceptRequester() throws JsonProcessingException {
        makeRequest("/all?from=0&size=20", List.of(new ItemRequestDto()));
        ResponseEntity<List<ItemRequestDto>> response = client.getAllExceptRequester(1L, 0, 20);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    public void getById() throws JsonProcessingException {
        makeRequest("/1", new ItemRequestDto());
        ResponseEntity<ItemRequestDto> response = client.getById(1L, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private <T> void makeRequest(String path, T dto) throws JsonProcessingException {
        String json = mapper.writeValueAsString(dto);

        server.expect(requestTo(url + path))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    }
}

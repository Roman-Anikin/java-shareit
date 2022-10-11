package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ItemClient.class)
public class ItemClientTest {

    private final String url = "http://localhost:9090/items";
    @Autowired
    private ItemClient client;
    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void add() throws JsonProcessingException {
        makeRequest("", new ItemDto());
        ResponseEntity<ItemDto> response = client.add(1L, new ItemDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update() throws JsonProcessingException {
        makeRequest("/1", new ItemDto());
        ResponseEntity<ItemDto> response = client.update(1L, 1L, new ItemDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getById() throws JsonProcessingException {
        makeRequest("/1", new OwnerItemDto());
        ResponseEntity<OwnerItemDto> response = client.getById(1L, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getByOwner() throws JsonProcessingException {
        makeRequest("?from=0&size=20", List.of(new OwnerItemDto()));
        ResponseEntity<List<OwnerItemDto>> response = client.getByOwner(1L, 0, 20);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    public void searchByText() throws JsonProcessingException {
        makeRequest("/search?text=text&from=0&size=20", List.of(new ItemDto()));
        ResponseEntity<List<ItemDto>> response = client.searchByText(1L, "text", 0, 20);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    public void addComment() throws JsonProcessingException {
        makeRequest("/1/comment", new CommentDto());
        ResponseEntity<CommentDto> response = client.addComment(1L, 1L, new CommentDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private <T> void makeRequest(String path, T dto) throws JsonProcessingException {
        String json = mapper.writeValueAsString(dto);

        server.expect(requestTo(url + path))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    }
}

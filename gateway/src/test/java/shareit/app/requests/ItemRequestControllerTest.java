package shareit.app.requests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import shareit.app.requests.dto.ItemRequestDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    private static final String URI = "/requests";
    private static final String HEADER = "X-Sharer-User-Id";
    @Autowired
    private WebTestClient testClient;
    @MockitoBean
    private ItemRequestClient requestClient;

    @Test
    void addRequest() {
        ItemRequestDto dto = new ItemRequestDto(1L, "desc", null, null);
        when(requestClient.add(any(), any())).thenReturn(Mono.just(dto));

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.description").isEqualTo(dto.getDescription());

        verify(requestClient).add(any(), any(ItemRequestDto.class));
        verifyNoMoreInteractions(requestClient);
    }

    @Test
    void addRequestWithoutHeader() {
        ItemRequestDto dto = new ItemRequestDto(1L, "desc", null, List.of());

        testClient.post()
                .uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addRequestWithBlankDescription() {
        ItemRequestDto dto = new ItemRequestDto(1L, "   ", null, List.of());

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addRequestWithEmptyDescription() {
        ItemRequestDto dto = new ItemRequestDto(1L, "", null, List.of());

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addRequestWithoutDescription() {
        ItemRequestDto dto = new ItemRequestDto(1L, null, null, List.of());

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getAllByRequester() {
        ItemRequestDto dto1 = new ItemRequestDto(1L, "desc1", null, null);
        ItemRequestDto dto2 = new ItemRequestDto(2L, "desc2", null, null);
        when(requestClient.getAllByRequester(any())).thenReturn(Mono.just(List.of(dto1, dto2)));

        testClient.get()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ItemRequestDto.class)
                .hasSize(2);

        verify(requestClient).getAllByRequester(any());
        verifyNoMoreInteractions(requestClient);
    }

    @Test
    void getAllByRequesterWithoutHeader() {
        testClient.get()
                .uri(URI)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getAllExceptRequester() {
        ItemRequestDto dto1 = new ItemRequestDto(1L, "desc1", null, null);
        ItemRequestDto dto2 = new ItemRequestDto(2L, "desc2", null, null);
        when(requestClient.getAllExceptRequester(any(), any(), any())).thenReturn(Mono.just(List.of(dto1, dto2)));

        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/all")
                        .queryParam("from", 0)
                        .queryParam("size", 5)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ItemRequestDto.class)
                .hasSize(2);

        verify(requestClient).getAllExceptRequester(any(), any(), any());
        verifyNoMoreInteractions(requestClient);
    }

    @Test
    void getAllExceptRequesterWithoutParams() {
        ItemRequestDto dto1 = new ItemRequestDto(1L, "desc1", null, null);
        ItemRequestDto dto2 = new ItemRequestDto(2L, "desc2", null, null);
        when(requestClient.getAllExceptRequester(any(), any(), any())).thenReturn(Mono.just(List.of(dto1, dto2)));

        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/all")
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ItemRequestDto.class)
                .hasSize(2);

        verify(requestClient).getAllExceptRequester(any(), any(), any());
        verifyNoMoreInteractions(requestClient);
    }

    @Test
    void getAllExceptRequesterWithoutHeader() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/all")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getAllExceptRequesterWithFromLessZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/all")
                        .queryParam("from", -10)
                        .queryParam("size", 5)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getAllExceptRequesterWithSizeZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/all")
                        .queryParam("from", 0)
                        .queryParam("size", 0)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getAllExceptRequesterWithSizeLessZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/all")
                        .queryParam("from", 0)
                        .queryParam("size", -10)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getById() {
        ItemRequestDto dto = new ItemRequestDto(1L, "desc", null, null);
        when(requestClient.getById(any(), any())).thenReturn(Mono.just(dto));

        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{requestId}")
                        .build(1L))
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.description").isEqualTo(dto.getDescription());

        verify(requestClient).getById(any(), any());
        verifyNoMoreInteractions(requestClient);
    }

    @Test
    void getByIdWithoutHeader() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{requestId}")
                        .build(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }
}

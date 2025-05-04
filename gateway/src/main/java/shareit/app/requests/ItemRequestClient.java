package shareit.app.requests;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import shareit.app.requests.dto.ItemRequestDto;

import java.util.List;

@Component
public class ItemRequestClient {

    private final WebClient client;
    private final String sharerHeader = "X-Sharer-User-Id";

    public ItemRequestClient(@Value("${share-it-server.url}/requests") String url) {
        client = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<ItemRequestDto> add(Long requesterId, ItemRequestDto requestDto) {
        return client.post()
                .header(sharerHeader, String.valueOf(requesterId))
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(ItemRequestDto.class);
    }

    public Mono<List<ItemRequestDto>> getAllByRequester(Long requesterId) {
        return client.get()
                .header(sharerHeader, String.valueOf(requesterId))
                .retrieve()
                .bodyToFlux(ItemRequestDto.class)
                .collectList();
    }

    public Mono<List<ItemRequestDto>> getAllExceptRequester(Long requesterId, Integer from, Integer size) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/all")
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(sharerHeader, String.valueOf(requesterId))
                .retrieve()
                .bodyToFlux(ItemRequestDto.class)
                .collectList();
    }

    public Mono<ItemRequestDto> getById(Long userId, Long requestId) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{requestId}")
                        .build(userId))
                .header(sharerHeader, String.valueOf(requestId))
                .retrieve()
                .bodyToMono(ItemRequestDto.class);
    }
}

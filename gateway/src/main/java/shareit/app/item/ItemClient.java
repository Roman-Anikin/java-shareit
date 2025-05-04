package shareit.app.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import shareit.app.item.dto.CommentDto;
import shareit.app.item.dto.ItemDto;
import shareit.app.item.dto.OwnerItemDto;

import java.util.List;

@Component
public class ItemClient {

    private final WebClient client;
    private final String sharerHeader = "X-Sharer-User-Id";

    public ItemClient(@Value("${share-it-server.url}/items") String url) {
        client = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<ItemDto> add(Long userId, ItemDto itemDto) {
        return client.post()
                .header(sharerHeader, String.valueOf(userId))
                .bodyValue(itemDto)
                .retrieve()
                .bodyToMono(ItemDto.class);
    }

    public Mono<ItemDto> update(Long itemId, Long userId, ItemDto itemDto) {
        return client.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/{itemId}")
                        .build(itemId))
                .header(sharerHeader, String.valueOf(userId))
                .bodyValue(itemDto)
                .retrieve()
                .bodyToMono(ItemDto.class);
    }

    public Mono<OwnerItemDto> getById(Long itemId, Long userId) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{itemId}")
                        .build(itemId))
                .header(sharerHeader, String.valueOf(userId))
                .retrieve()
                .bodyToMono(OwnerItemDto.class);
    }

    public Mono<List<OwnerItemDto>> getByOwner(Long userId, Integer from, Integer size) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(sharerHeader, String.valueOf(userId))
                .retrieve()
                .bodyToFlux(OwnerItemDto.class)
                .collectList();
    }

    public Mono<List<ItemDto>> searchByText(Long userId, String text, Integer from, Integer size) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("text", text)
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(sharerHeader, String.valueOf(userId))
                .retrieve()
                .bodyToFlux(ItemDto.class)
                .collectList();
    }

    public Mono<CommentDto> addComment(Long itemId, Long userId, CommentDto commentDto) {
        return client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/{itemId}/comment")
                        .build(itemId))
                .header(sharerHeader, String.valueOf(userId))
                .bodyValue(commentDto)
                .retrieve()
                .bodyToMono(CommentDto.class);
    }
}

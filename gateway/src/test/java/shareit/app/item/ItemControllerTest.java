package shareit.app.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import shareit.app.item.dto.CommentDto;
import shareit.app.item.dto.ItemDto;
import shareit.app.item.dto.OwnerItemDto;

import java.util.List;

import static org.mockito.Mockito.*;

@WebFluxTest(ItemController.class)
public class ItemControllerTest {

    private static final String URI = "/items";
    private static final String HEADER = "X-Sharer-User-Id";
    @Autowired
    private WebTestClient testClient;
    @MockitoBean
    private ItemClient itemClient;

    @Test
    void addItem() {
        ItemDto item = new ItemDto(1L, "item", "desc", true, null);
        when(itemClient.add(any(), any(ItemDto.class))).thenReturn(Mono.just(item));

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo(item.getName())
                .jsonPath("$.description").isEqualTo(item.getDescription());

        verify(itemClient).add(any(), any(ItemDto.class));
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void addItemWithoutName() {
        ItemDto item = new ItemDto(1L, null, "desc", true, null);

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addItemWithEmptyName() {
        ItemDto item = new ItemDto(1L, "", "desc", true, null);

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addItemWithBlankName() {
        ItemDto item = new ItemDto(1L, "   ", "desc", true, null);

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addItemWithoutDescription() {
        ItemDto item = new ItemDto(1L, "item", null, true, null);

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addItemWithEmptyDescription() {
        ItemDto item = new ItemDto(1L, "item", "", true, null);

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addItemWithBlankDescription() {
        ItemDto item = new ItemDto(1L, "item", "   ", true, null);

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addItemWithoutAvailable() {
        ItemDto item = new ItemDto(1L, "item", "desc", null, null);

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addItemWithoutHeader() {
        ItemDto item = new ItemDto(1L, "item", "desc", true, null);

        testClient.post()
                .uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateItem() {
        ItemDto item = new ItemDto(1L, "item", "desc", true, null);
        when(itemClient.update(any(), any(), any(ItemDto.class))).thenReturn(Mono.just(item));

        testClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{itemId}")
                        .build(item.getId()))
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo(item.getName())
                .jsonPath("$.description").isEqualTo(item.getDescription());

        verify(itemClient).update(any(), any(), any(ItemDto.class));
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void updateItemNameOnly() {
        ItemDto item = new ItemDto(1L, "item", null, null, null);
        when(itemClient.update(any(), any(), any(ItemDto.class))).thenReturn(Mono.just(item));

        testClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{itemId}")
                        .build(item.getId()))
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo(item.getName())
                .jsonPath("$.description").isEqualTo(item.getDescription());

        verify(itemClient).update(any(), any(), any(ItemDto.class));
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void updateItemDescriptionOnly() {
        ItemDto item = new ItemDto(1L, null, "desc", null, null);
        when(itemClient.update(any(), any(), any(ItemDto.class))).thenReturn(Mono.just(item));

        testClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{itemId}")
                        .build(item.getId()))
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo(item.getName())
                .jsonPath("$.description").isEqualTo(item.getDescription());

        verify(itemClient).update(any(), any(), any(ItemDto.class));
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void updateItemAvailableOnly() {
        ItemDto item = new ItemDto(1L, null, null, true, null);
        when(itemClient.update(any(), any(), any(ItemDto.class))).thenReturn(Mono.just(item));

        testClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{itemId}")
                        .build(item.getId()))
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo(item.getName())
                .jsonPath("$.description").isEqualTo(item.getDescription());

        verify(itemClient).update(any(), any(), any(ItemDto.class));
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void updateItemWithoutHeader() {
        ItemDto item = new ItemDto(1L, "item", "desc", null, null);

        testClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{itemId}")
                        .build(item.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getItemById() {
        OwnerItemDto item = new OwnerItemDto(1L, "item", "desc", true,
                null, null, null, null);
        when(itemClient.getById(any(), any())).thenReturn(Mono.just(item));

        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{itemId}")
                        .build(item.getId()))
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo(item.getName())
                .jsonPath("$.description").isEqualTo(item.getDescription());

        verify(itemClient).getById(any(), any());
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void getItemByIdWithoutHeader() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{itemId}")
                        .build(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getItemsByOwner() {
        OwnerItemDto itemDto = new OwnerItemDto(1L, "item", "desc", true,
                null, null, null, null);
        OwnerItemDto itemDto2 = new OwnerItemDto(2L, "new item", "new desc", false,
                null, null, null, null);
        when(itemClient.getByOwner(any(), any(), any()))
                .thenReturn(Mono.just(List.of(itemDto, itemDto2)));

        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI)
                        .queryParam("from", 0)
                        .queryParam("size", 5)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(OwnerItemDto.class)
                .hasSize(2);

        verify(itemClient).getByOwner(any(), any(), any());
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void getItemsByOwnerWithoutHeader() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI)
                        .queryParam("from", 0)
                        .queryParam("size", 5)
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getItemsByOwnerWithFromLessZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI)
                        .queryParam("from", -10)
                        .queryParam("size", 5)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getItemsByOwnerWithSizeZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI)
                        .queryParam("from", 0)
                        .queryParam("size", 0)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getItemsByOwnerWithSizeLessZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI)
                        .queryParam("from", 0)
                        .queryParam("size", -10)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getItemsBySearch() {
        ItemDto itemDto = new ItemDto(1L, "item", "desc", true, null);
        ItemDto itemDto2 = new ItemDto(2L, "new item", "new desc", false, null);
        when(itemClient.searchByText(any(), any(), any(), any()))
                .thenReturn(Mono.just(List.of(itemDto, itemDto2)));

        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/search")
                        .queryParam("text", "text")
                        .queryParam("from", 0)
                        .queryParam("size", 5)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ItemDto.class)
                .hasSize(2);

        verify(itemClient).searchByText(any(), any(), any(), any());
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void getItemsBySearchWithoutText() {
        when(itemClient.searchByText(any(), any(), any(), any()))
                .thenReturn(Mono.just(List.of()));

        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/search")
                        .queryParam("from", 0)
                        .queryParam("size", 5)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ItemDto.class)
                .hasSize(0);

        verify(itemClient).searchByText(any(), any(), any(), any());
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void getItemsBySearchWithoutHeader() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/search")
                        .queryParam("from", 0)
                        .queryParam("size", 5)
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getItemsBySearchWithFromLessZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/search")
                        .queryParam("from", -10)
                        .queryParam("size", 5)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getItemsBySearchWithSizeZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/search")
                        .queryParam("from", 0)
                        .queryParam("size", 0)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getItemsBySearchWithSizeLessZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/search")
                        .queryParam("from", 0)
                        .queryParam("size", -10)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addComment() {
        CommentDto comment = new CommentDto(1L, "com", "name", null);
        when(itemClient.addComment(any(), any(), any())).thenReturn(Mono.just(comment));

        testClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{itemId}/comment")
                        .build(1L))
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(comment)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.text").isEqualTo(comment.getText());

        verify(itemClient).addComment(any(), any(), any());
        verifyNoMoreInteractions(itemClient);

    }

    @Test
    void addCommentWithoutText() {
        CommentDto comment = new CommentDto(1L, null, "name", null);

        testClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{itemId}/comment")
                        .build(1L))
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(comment)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addCommentWithEmptyText() {
        CommentDto comment = new CommentDto(1L, "", "name", null);

        testClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{itemId}/comment")
                        .build(1L))
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(comment)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addCommentWithBlankText() {
        CommentDto comment = new CommentDto(1L, "   ", "name", null);

        testClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{itemId}/comment")
                        .build(1L))
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(comment)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addCommentWithoutHeader() {
        CommentDto comment = new CommentDto(1L, "text", "name", null);

        testClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{itemId}/comment")
                        .build(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(comment)
                .exchange()
                .expectStatus().isBadRequest();
    }
}

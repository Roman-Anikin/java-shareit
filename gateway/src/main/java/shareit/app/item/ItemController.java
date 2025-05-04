package shareit.app.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import shareit.app.item.dto.CommentDto;
import shareit.app.item.dto.ItemDto;
import shareit.app.item.dto.OwnerItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
@AllArgsConstructor
public class ItemController {

    private ItemClient client;

    @PostMapping
    public Mono<ItemDto> add(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        return client.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Mono<ItemDto> update(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                @RequestBody ItemDto itemDto) {
        return client.update(itemId, ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public Mono<OwnerItemDto> getById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getById(itemId, userId);
    }

    @GetMapping
    public Mono<List<OwnerItemDto>> getByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                               @RequestParam(value = "from", defaultValue = "0")
                                               @PositiveOrZero Integer from,
                                               @RequestParam(value = "size", defaultValue = "200")
                                               @Positive Integer size) {
        return client.getByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public Mono<List<ItemDto>> searchByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(required = false) String text,
                                            @RequestParam(value = "from", defaultValue = "0")
                                            @PositiveOrZero Integer from,
                                            @RequestParam(value = "size", defaultValue = "200")
                                            @Positive Integer size) {
        return client.searchByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public Mono<CommentDto> addComment(@PathVariable Long itemId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId,
                                       @Valid @RequestBody CommentDto commentDto) {
        return client.addComment(itemId, userId, commentDto);
    }
}

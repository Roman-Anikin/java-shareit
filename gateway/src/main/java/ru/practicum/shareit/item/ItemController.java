package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    @Autowired
    private ItemClient client;

    @PostMapping
    public ResponseEntity<ItemDto> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @Valid @RequestBody ItemDto itemDto) {
        return client.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @RequestBody ItemDto itemDto) {
        return client.update(itemId, ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<OwnerItemDto> getById(@PathVariable Long itemId,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<List<OwnerItemDto>> getByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                         @RequestParam(value = "from", defaultValue = "0")
                                                         @PositiveOrZero Integer from,
                                                         @RequestParam(value = "size", defaultValue = "200")
                                                         @Positive Integer size) {
        return client.getByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(required = false) String text,
                                                      @RequestParam(value = "from", defaultValue = "0")
                                                      @PositiveOrZero Integer from,
                                                      @RequestParam(value = "size", defaultValue = "200")
                                                      @Positive Integer size) {
        return client.searchByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@PathVariable Long itemId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody CommentDto commentDto) {
        return client.addComment(itemId, userId, commentDto);
    }
}

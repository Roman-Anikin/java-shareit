package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService itemService) {
        this.service = itemService;
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        return service.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @RequestBody ItemDto itemDto) {
        return service.update(itemId, ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public OwnerItemDto getById(@PathVariable Long itemId,
                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getById(userId, itemId);
    }

    @GetMapping
    public List<OwnerItemDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                         @RequestParam(value = "from", defaultValue = "0") Integer from,
                                         @RequestParam(value = "size", required = false) Integer size) {
        return service.getByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam(required = false) String text,
                                      @RequestParam(value = "from", defaultValue = "0") Integer from,
                                      @RequestParam(value = "size", required = false) Integer size) {
        return service.searchByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return service.addComment(itemId, userId, commentDto);
    }
}

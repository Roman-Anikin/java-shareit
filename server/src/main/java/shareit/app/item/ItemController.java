package shareit.app.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shareit.app.item.dto.CommentDto;
import shareit.app.item.dto.ItemDto;
import shareit.app.item.dto.OwnerItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody ItemDto itemDto) {
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
                                         @RequestParam(value = "from") Integer from,
                                         @RequestParam(value = "size") Integer size) {
        return service.getByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam(required = false) String text,
                                      @RequestParam(value = "from") Integer from,
                                      @RequestParam(value = "size") Integer size) {
        return service.searchByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody CommentDto commentDto) {
        return service.addComment(itemId, userId, commentDto);
    }
}

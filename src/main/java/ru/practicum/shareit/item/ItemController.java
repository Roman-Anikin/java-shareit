package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        itemDto.setOwnerId(userId);
        return convertToDto(itemService.add(convertFromDto(itemDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @RequestBody ItemDto itemDto) {
        return convertToDto(itemService.update(itemId, ownerId, convertFromDto(itemDto)));
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        return convertToDto(itemService.getById(itemId));
    }

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getByOwner(ownerId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam(required = false) String text) {
        return itemService.searchByText(text)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private Item convertFromDto(ItemDto itemDto) {
        Item item = new Item(null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                new User(),
                itemDto.getItemRequest());
        item.getOwner().setId(itemDto.getOwnerId());
        return item;
    }

    private ItemDto convertToDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getItemRequest());
    }
}

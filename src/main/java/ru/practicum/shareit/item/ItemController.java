package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    public ItemController(ItemService itemService, ItemMapper itemConverter) {
        this.itemService = itemService;
        this.itemMapper = itemConverter;
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        Item item = itemMapper.convertFromDto(itemDto);
        item.getOwner().setId(userId);
        return itemMapper.convertToDto(itemService.add(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @RequestBody ItemDto itemDto) {
        return itemMapper.convertToDto(itemService.update(itemId, ownerId, itemMapper.convertFromDto(itemDto)));
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        return itemMapper.convertToDto(itemService.getById(itemId));
    }

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getByOwner(ownerId)
                .stream()
                .map(itemMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam(required = false) String text) {
        return itemService.searchByText(text)
                .stream()
                .map(itemMapper::convertToDto)
                .collect(Collectors.toList());
    }
}

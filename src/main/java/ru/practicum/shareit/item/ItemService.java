package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(Long userId, ItemDto item);

    ItemDto update(Long itemId, Long ownerId, ItemDto item);

    OwnerItemDto getById(Long userId, Long itemId);

    List<OwnerItemDto> getByOwner(Long ownerId);

    List<ItemDto> searchByText(String text);

    Item getItemById(Long itemId);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);
}

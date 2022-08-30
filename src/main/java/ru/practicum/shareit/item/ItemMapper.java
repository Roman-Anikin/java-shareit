package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public Item convertFromDto(ItemDto itemDto) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                new User(),
                itemDto.getItemRequest());
    }

    public ItemDto convertToDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getItemRequest());
    }

    public List<ItemDto> convertToDto(List<Item> items) {
        return items
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}

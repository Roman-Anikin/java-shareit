package ru.practicum.shareit.item;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
public class ItemConverter {

    private final ModelMapper mapper;

    public ItemConverter(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Item convertFromDto(ItemDto itemDto) {
        return mapper.map(itemDto, Item.class);
    }

    public ItemDto convertToDto(Item item) {
        return mapper.map(item, ItemDto.class);
    }
}

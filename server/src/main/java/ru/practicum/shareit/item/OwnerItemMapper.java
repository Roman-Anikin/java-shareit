package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.OwnerItemDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OwnerItemMapper {

    public OwnerItemDto convertToDto(Item item) {
        return new OwnerItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>(),
                item.getRequest());
    }

    public List<OwnerItemDto> convertToDto(List<Item> items) {
        return items.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}

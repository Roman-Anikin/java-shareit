package shareit.app.item;

import org.springframework.stereotype.Component;
import shareit.app.item.dto.ItemDto;
import shareit.app.requests.ItemRequest;
import shareit.app.user.User;

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
                itemDto.getRequestId() == null ? null :
                        new ItemRequest(itemDto.getRequestId(), null, null, null));
    }

    public ItemDto convertToDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() == null ? null : item.getRequest().getId());
    }

    public List<ItemDto> convertToDto(List<Item> items) {
        return items
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
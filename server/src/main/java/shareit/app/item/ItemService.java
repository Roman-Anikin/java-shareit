package shareit.app.item;

import shareit.app.item.dto.CommentDto;
import shareit.app.item.dto.ItemDto;
import shareit.app.item.dto.OwnerItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(Long userId, ItemDto item);

    ItemDto update(Long itemId, Long ownerId, ItemDto item);

    OwnerItemDto getById(Long userId, Long itemId);

    List<OwnerItemDto> getByOwner(Long ownerId, Integer from, Integer size);

    List<ItemDto> searchByText(Long userId, String text, Integer from, Integer size);

    Item getItemById(Long itemId);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);

    List<ItemDto> getAllByRequestId(Long requesterId);
}

package shareit.app.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shareit.app.booking.BookingService;
import shareit.app.exception.ObjectNotFoundException;
import shareit.app.item.dto.CommentDto;
import shareit.app.item.dto.ItemDto;
import shareit.app.item.dto.OwnerItemDto;
import shareit.app.user.UserMapper;
import shareit.app.user.UserService;
import shareit.app.util.OffsetPageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Lazy})
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;
    private final BookingService bookingService;
    private final CommentService commentService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final OwnerItemMapper ownerItemMapper;

    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        userService.getById(userId);
        Item item = itemMapper.convertFromDto(itemDto);
        item.getOwner().setId(userId);
        repository.save(item);
        log.info("Добавлен предмет {}", item);
        return itemMapper.convertToDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long ownerId, ItemDto itemDto) {
        userService.getById(ownerId);
        Item item = repository.getByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() ->
                        new ObjectNotFoundException("Предмет с id " + itemId + " не найден"));
        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);
        log.info("Обновлен предмет {}", item);
        return itemMapper.convertToDto(item);
    }

    @Override
    public OwnerItemDto getById(Long userId, Long itemId) {
        Item item = checkItem(itemId);
        OwnerItemDto itemDto = ownerItemMapper.convertToDto(item);
        itemDto.setComments(commentService.getAllByItemId(itemId));
        if (item.getOwner().getId().equals(userId)) {
            itemDto.setLastBooking(bookingService.getLastBooking(itemId));
            itemDto.setNextBooking(bookingService.getNextBooking(itemId));
        }
        log.info("Получен предмет {}", itemDto);
        return itemDto;
    }

    @Override
    public List<OwnerItemDto> getByOwner(Long ownerId, Integer from, Integer size) {
        List<OwnerItemDto> items = ownerItemMapper.convertToDto(repository.getAllByOwnerId(ownerId,
                getPagination(from, size, "id")));
        for (OwnerItemDto item : items) {
            item.setLastBooking(bookingService.getLastBooking(item.getId()));
            item.setNextBooking(bookingService.getNextBooking(item.getId()));
        }
        log.info("Получен список предметов {} пользователя {}", items, userService.getById(ownerId));
        return items;
    }

    @Override
    public List<ItemDto> searchByText(Long userId, String text, Integer from, Integer size) {
        userService.getById(userId);
        text = text.toLowerCase().trim();
        List<Item> items = new ArrayList<>();
        if (!text.isEmpty()) {
            items = repository.getAllByText(text, getPagination(from, size, "item_id"));
        }
        log.info("Получен список предметов {} по поиску {}", items, text);
        return itemMapper.convertToDto(items);
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        return commentService.add(itemId, userId, commentDto);
    }

    @Override
    public List<ItemDto> getAllByRequestId(Long requestId) {
        List<Item> items = repository.getAllByRequestId(requestId);
        log.info("Получен список предметов {} по запросу {}", items, requestId);
        return items == null ? new ArrayList<>() : itemMapper.convertToDto(items);
    }

    @Override
    public Item getItemById(Long itemId) {
        Item item = checkItem(itemId);
        item.setOwner(userMapper.convertFromDto(userService.getById(item.getOwner().getId())));
        return item;
    }

    private Item checkItem(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() ->
                        new ObjectNotFoundException("Предмет с id " + itemId + " не найден"));
    }

    private Pageable getPagination(Integer from, Integer size, String properties) {
        return new OffsetPageRequest(from, size, Sort.by(Sort.Direction.ASC, properties));
    }
}
package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.util.OffsetPageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;
    private final BookingService bookingService;
    private final CommentService commentService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final OwnerItemMapper ownerItemMapper;

    public ItemServiceImpl(ItemRepository repository,
                           UserService userService,
                           @Lazy BookingService bookingService,
                           @Lazy CommentService commentService,
                           ItemMapper itemMapper,
                           UserMapper userMapper,
                           OwnerItemMapper ownerItemMapper) {
        this.repository = repository;
        this.userService = userService;
        this.bookingService = bookingService;
        this.commentService = commentService;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
        this.ownerItemMapper = ownerItemMapper;
    }

    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        checkUser(userId);
        Item item = itemMapper.convertFromDto(itemDto);
        item.getOwner().setId(userId);
        log.info("Добавлен предмет {}", item);
        return itemMapper.convertToDto(repository.save(item));
    }

    @Override
    public ItemDto update(Long itemId, Long ownerId, ItemDto itemDto) {
        checkUser(ownerId);
        if (repository.getByOwnerId(ownerId, Pageable.unpaged())
                .stream()
                .anyMatch(item -> item.getId().equals(itemId))) {
            Item newItem = getItemById(itemId);
            newItem.setId(itemId);
            if (itemDto.getName() != null) {
                newItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                newItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                newItem.setAvailable(itemDto.getAvailable());
            }
            log.info("Обновлен предмет {}", newItem);
            return itemMapper.convertToDto(repository.save(newItem));
        }
        throw new ObjectNotFoundException("Предмет с id " + itemId + " не найден");
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
        List<OwnerItemDto> items = ownerItemMapper.convertToDto(repository.getByOwnerId(ownerId,
                getPagination(from, size)));
        for (OwnerItemDto item : items) {
            item.setLastBooking(bookingService.getLastBooking(item.getId()));
            item.setNextBooking(bookingService.getNextBooking(item.getId()));
        }
        log.info("Получен список предметов {} пользователя {}", items, userService.getById(ownerId));
        return items;
    }

    @Override
    public List<ItemDto> searchByText(Long userId, String text, Integer from, Integer size) {
        checkUser(userId);
        text = text.toLowerCase().trim();
        List<Item> items = new ArrayList<>();
        if (!text.isEmpty()) {
            items = repository.searchByText(text, getPagination(from, size));
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

    private void checkUser(Long userId) {
        if (userService.getById(userId) == null) {
            throw new ObjectNotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    private Item checkItem(Long itemId) {
        Optional<Item> item = repository.findById(itemId);
        if (item.isEmpty()) {
            throw new ObjectNotFoundException("Предмет с id " + itemId + " не найден");
        }
        return item.get();
    }

    private Pageable getPagination(Integer from, Integer size) {
        return new OffsetPageRequest(from, size, Sort.by(Sort.Direction.ASC, "item_id"));
    }
}
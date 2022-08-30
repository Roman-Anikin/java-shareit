package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

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
        Item item = itemMapper.convertFromDto(itemDto);
        item.getOwner().setId(userId);
        if (userService.getById(userId) != null) {
            item.setOwner(userMapper.convertFromDto(userService.getById(userId)));
            log.info("Добавлен предмет {}", item);
            return itemMapper.convertToDto(repository.save(item));
        }
        throw new ObjectNotFoundException("Пользователь с id " + userId + " не найден");
    }

    @Override
    public ItemDto update(Long itemId, Long ownerId, ItemDto itemDto) {
        if (userService.getById(ownerId) != null) {
            if (repository.getByOwnerIdOrderByIdAsc(ownerId)
                    .stream()
                    .anyMatch(item1 -> item1.getId().equals(itemId))) {
                Item newItem = getItemById(itemId);
                newItem.setId(itemId);
                newItem.setOwner(userMapper.convertFromDto(userService.getById(ownerId)));
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
        throw new ObjectNotFoundException("Владелец с id " + ownerId + " не найден");
    }

    @Override
    public OwnerItemDto getById(Long userId, Long itemId) {
        Optional<Item> item = repository.findById(itemId);
        if (item.isPresent()) {
            OwnerItemDto itemDto = ownerItemMapper.convertToDto(item.get());
            itemDto.setComments(commentService.getAllByItemId(itemId));
            if (item.get().getOwner().getId().equals(userId)) {
                itemDto.setLastBooking(bookingService.getLastBooking(userId, itemId));
                itemDto.setNextBooking(bookingService.getNextBooking(userId, itemId));
            }
            log.info("Получен предмет {}", itemDto);
            return itemDto;
        }
        throw new ObjectNotFoundException("Предмет с id " + itemId + " не найден");
    }

    @Override
    public List<OwnerItemDto> getByOwner(Long ownerId) {
        List<OwnerItemDto> items = ownerItemMapper.convertToDto(repository.getByOwnerIdOrderByIdAsc(ownerId));
        for (OwnerItemDto item : items) {
            item.setLastBooking(bookingService.getLastBooking(ownerId, item.getId()));
            item.setNextBooking(bookingService.getNextBooking(ownerId, item.getId()));
        }
        log.info("Получен список предметов {} пользователя {}", items, userService.getById(ownerId));
        return items;
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        text = text.toLowerCase().trim();
        List<Item> items = new ArrayList<>();
        if (!text.isEmpty()) {
            items = repository.searchByText(text);
        }
        log.info("Получен список предметов {} по поиску {}", items, text);
        return itemMapper.convertToDto(items);
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        return commentService.add(itemId, userId, commentDto);
    }

    @Override
    public Item getItemById(Long itemId) {
        Optional<Item> item = repository.findById(itemId);
        if (item.isPresent()) {
            item.get().setOwner(userMapper.convertFromDto(userService.getById(item.get().getOwner().getId())));
            return item.get();
        }
        throw new ObjectNotFoundException("Предмет с id " + itemId + " не найден");
    }
}
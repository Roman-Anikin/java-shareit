package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    public CommentServiceImpl(CommentRepository repository,
                              UserService userService,
                              ItemService itemService,
                              BookingService bookingService,
                              CommentMapper commentMapper,
                              UserMapper userMapper) {
        this.repository = repository;
        this.userService = userService;
        this.itemService = itemService;
        this.bookingService = bookingService;
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
    }

    @Override
    public CommentDto add(Long itemId, Long userId, CommentDto commentDto) {
        if (userService.getById(userId) != null) {
            if (itemService.getItemById(itemId) != null) {
                if (bookingService.getByItemId(itemId, userId, LocalDateTime.now()) != null) {
                    Comment comment = commentMapper.convertFromDto(commentDto);
                    comment.setAuthor(userMapper.convertFromDto(userService.getById(userId)));
                    comment.setItem(itemService.getItemById(itemId));
                    comment.setCreated(LocalDateTime.now());
                    log.info("Добавлен комментарий {}", comment);
                    return commentMapper.convertToDto(repository.save(comment));
                }
                throw new ValidationException("Бронирование предмета с id " + itemId + " не найдено");
            }
            throw new ObjectNotFoundException("Предмет с id " + itemId + " не найден");
        }
        throw new ObjectNotFoundException("Пользователь с id " + userId + " не найден");
    }

    @Override
    public List<CommentDto> getAllByItemId(Long itemId) {
        return repository.findAllByItemId(itemId)
                .stream()
                .map(commentMapper::convertToDto)
                .collect(Collectors.toList());
    }
}
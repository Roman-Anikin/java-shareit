package shareit.app.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shareit.app.booking.BookingService;
import shareit.app.item.dto.CommentDto;
import shareit.app.user.UserMapper;
import shareit.app.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    @Override
    public CommentDto add(Long itemId, Long userId, CommentDto commentDto) {
        bookingService.getByItemId(itemId, userId, LocalDateTime.now());
        Comment comment = commentMapper.convertFromDto(commentDto);
        comment.setAuthor(userMapper.convertFromDto(userService.getById(userId)));
        comment.setItem(itemService.getItemById(itemId));
        comment.setCreated(LocalDateTime.now());
        repository.save(comment);
        log.info("Добавлен комментарий {}", comment);
        return commentMapper.convertToDto(comment);
    }

    @Override
    public List<CommentDto> getAllByItemId(Long itemId) {
        return repository.findAllByItemId(itemId)
                .stream()
                .map(commentMapper::convertToDto)
                .collect(Collectors.toList());
    }
}
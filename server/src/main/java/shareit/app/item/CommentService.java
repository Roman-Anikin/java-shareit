package shareit.app.item;

import shareit.app.item.dto.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto add(Long itemId, Long userId, CommentDto commentDto);

    List<CommentDto> getAllByItemId(Long itemId);
}

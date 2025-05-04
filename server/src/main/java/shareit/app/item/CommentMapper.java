package shareit.app.item;

import org.springframework.stereotype.Component;
import shareit.app.item.dto.CommentDto;
import shareit.app.user.User;

@Component
public class CommentMapper {

    public Comment convertFromDto(CommentDto comment) {
        return new Comment(comment.getId(),
                comment.getText(),
                new Item(),
                new User(),
                null);
    }

    public CommentDto convertToDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}
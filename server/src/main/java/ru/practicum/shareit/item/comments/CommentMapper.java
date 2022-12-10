package ru.practicum.shareit.item.comments;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreationDate()
        );
    }

    public static Comment toComment(CommentDto commentDto, Item item, User user) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                item,
                user,
                commentDto.getCreationDate()
        );
    }
}

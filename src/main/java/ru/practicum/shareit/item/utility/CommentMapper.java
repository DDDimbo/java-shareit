package ru.practicum.shareit.item.utility;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(CommentDto commentDto, User user, Long itemId) {
        return Comment.builder()
                .text(commentDto.getText())
                .itemId(itemId)
                .author(user)
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .itemId(comment.getItemId())
                .build();
    }
}

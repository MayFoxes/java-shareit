package ru.practicum.shareit.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.model.Comment;

@UtilityClass
public class CommentMapper {
    public CommentExtendedDto toCommentDto(Comment comment) {
        return CommentExtendedDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .user(comment.getUser())
                .item(comment.getItem())
                .authorName(comment.getAuthorName())
                .created(comment.getCreated())
                .build();
    }

    public Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
    }
}

package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.model.Comment;

public class CommentMapper {
    public static CommentExtendedDto toCommentDto(Comment comment) {
        return CommentExtendedDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .user(comment.getUser())
                .item(comment.getItem())
                .authorName(comment.getAuthorName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
    }
}

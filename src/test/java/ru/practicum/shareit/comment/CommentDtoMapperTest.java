package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentExtendedDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CommentDtoMapperTest {
    @Test
    void toOutgoingDtoTest() {
        User user = User.builder()
                .id(1L)
                .name("username")
                .email("email@yandex.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .description("description")
                .name("itemname")
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .created(LocalDateTime.now())
                .user(user)
                .item(item)
                .authorName(user.getName())
                .build();
        CommentExtendedDto expected = CommentExtendedDto.builder()
                .id(1L)
                .text(comment.getText())
                .created(comment.getCreated())
                .item(item)
                .user(user)
                .authorName(user.getName())
                .build();

        CommentExtendedDto actual = CommentMapper.toCommentDto(comment);

        assertEquals(expected, actual);
    }

    @Test
    void toCommentTest() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .created(LocalDateTime.now())
                .build();
        Comment expected = Comment.builder()
                .id(1L)
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();

        Comment actual = CommentMapper.toComment(commentDto);

        assertEquals(expected, actual);
    }
}
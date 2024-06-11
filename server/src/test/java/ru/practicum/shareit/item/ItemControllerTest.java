package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    @MockBean
    private RequestRepository requestRepository;
    private User user;
    private ItemDto itemDto;
    private Item item;

    @SneakyThrows
    @Test
    void createItemTest() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        item = ItemMapper.toItem(itemDto);
        when(itemService.saveItem(any(Long.class), any()))
                .thenReturn(ItemMapper.toItemDto(item));

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @SneakyThrows
    @Test
    void updateItemTest() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        item = ItemMapper.toItem(itemDto);

        when(itemService.updateItem(any(Long.class), anyLong(), any(ItemDto.class)))
                .thenReturn(ItemMapper.toItemDto(item));
        when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(ItemMapper.toItemDto(item));

        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @SneakyThrows
    @Test
    void getItemTest() {
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .build();
        when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(ItemMapper.toItemDto(item));

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    @SneakyThrows
    void getUserItems() {
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .build();
        when(itemService.findItemsByOwner(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(ItemMapper.toItemDto(item)));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(item.getName())))
                .andExpect(jsonPath("$.[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(item.getAvailable())));
    }

    @Test
    @SneakyThrows
    void searchItems() {
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .build();
        when(itemService.findItemsByName(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(ItemMapper.toItemDto(item)));

        mvc.perform(get("/items/search?text=name")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(item.getName())))
                .andExpect(jsonPath("$.[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(item.getAvailable())));
    }

    @Test
    @SneakyThrows
    void createComment() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@mail.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .build();
        CommentDto commentDto = CommentDto.builder()
                .item(item.getId())
                .user(user.getId())
                .text("text")
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .user(user)
                .authorName(user.getName())
                .build();
        when(itemService.createComment(any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(CommentMapper.toCommentDto(comment));

        mvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("$.user.id", is(comment.getUser().getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void updateItemValidationFailedTest() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        item = ItemMapper.toItem(itemDto);

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenThrow(ValidationException.class);
        when(itemService.findItemById(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    throw new ValidationException("");
                });

        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateItemNoAccessTest() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        item = ItemMapper.toItem(itemDto);

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenThrow(NotFoundException.class);
        when(itemService.findItemById(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    throw new NotFoundException("Error");
                });

        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void updateItemNotFoundTest() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        item = ItemMapper.toItem(itemDto);

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenThrow(NotFoundException.class);
        when(itemService.findItemById(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    throw new NotFoundException("Error");
                });

        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

}
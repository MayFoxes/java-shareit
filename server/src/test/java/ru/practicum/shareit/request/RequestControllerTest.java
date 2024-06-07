package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    RequestService requestService;
    @Autowired
    private MockMvc mockMvc;
    private Request request;
    private User user1;
    private User user2;
    private Item item;
    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = RequestDto.builder()
                .description("need item")
                .build();
        user1 = User.builder()
                .id(1L)
                .name("username")
                .email("mail@mail.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("user2name")
                .email("mail2@mail.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("itemname")
                .description("desc")
                .available(true)
                .request(request)
                .owner(user1)
                .build();
        request = Request.builder()
                .id(1L)
                .user(user2)
                .description("need item")
                .build();
    }

    @SneakyThrows
    @Test
    void createTest() {
        when(requestService.createRequest(anyLong(), any(RequestDto.class)))
                .thenReturn(request);
        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())));
    }

    @SneakyThrows
    @Test
    void getUserRequestsTest() {
        when(requestService.findRequestsByOwnerId(anyLong()))
                .thenReturn(List.of(RequestMapper.toExtendedRequest(request)));
        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(request.getDescription())));
    }

    @SneakyThrows
    @Test
    void getByIdTest() {
        when(requestService.findRequestById(anyLong(), anyLong()))
                .thenReturn(RequestMapper.toExtendedRequest(request));
        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())));
    }

    @SneakyThrows
    @Test
    void getAllTest() {
        when(requestService.findAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(RequestMapper.toExtendedRequest(request)));
        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(request.getDescription())));
    }
}
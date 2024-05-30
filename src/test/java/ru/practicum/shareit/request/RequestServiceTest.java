package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExtendedDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User user;

    private RequestDto requestDto;

    @Test
    void createRequestUserNotFoundTest() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@mail.ru")
                .build();
        requestDto = RequestDto.builder()
                .created(LocalDateTime.now())
                .description("desc")
                .build();
        when(requestRepository.save(RequestMapper.toRequest(requestDto)))
                .thenReturn(RequestMapper.toRequest(requestDto));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.createRequest(user.getId(), requestDto));
        assertEquals("User:1 does not exist.", e.getMessage());
    }

    @Test
    void getByUserTest() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@mail.ru")
                .build();
        requestDto = RequestDto.builder()
                .id(1L)
                .description("desc")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByUserId(any(Long.class), any(Sort.class)))
                .thenReturn(List.of(RequestMapper.toRequest(requestDto)));
        when(itemRepository.findAllByRequest(RequestMapper.toRequest(requestDto)))
                .thenReturn(List.of(Item.builder()
                        .owner(user.getId())
                        .available(true)
                        .name("name")
                        .request(RequestMapper.toRequest(requestDto))
                        .owner(user.getId())
                        .build()));
        List<RequestExtendedDto> expected = List.of(RequestMapper.toExtendedRequest(RequestMapper.toRequest(requestDto)));
        expected.get(0).setItems(
                Stream.of(Item.builder()
                                .owner(user.getId())
                                .available(true)
                                .name("name")
                                .request(RequestMapper.toRequest(requestDto))
                                .owner(user.getId())
                                .build()).map(ItemMapper::toItemDto)
                        .collect(Collectors.toList()));

        List<RequestExtendedDto> actual = requestService.findRequestsByOwnerId(user.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getByUserNotFoundTest() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@mail.ru")
                .build();
        requestDto = RequestDto.builder()
                .id(1L)
                .description("desc")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.findRequestsByOwnerId(user.getId()));
    }

    @Test
    void getItemByIdTest() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@mail.ru")
                .build();
        requestDto = RequestDto.builder()
                .id(1L)
                .description("desc")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(RequestMapper.toRequest(requestDto)));

        RequestExtendedDto expected = RequestMapper.toExtendedRequest(RequestMapper.toRequest(requestDto));
        expected.setItems(List.of());

        RequestExtendedDto actual = requestService.findRequestById(1L, 1L);

        assertEquals(expected, actual);
    }

    @Test
    void getItemByIdNotFoundTest() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@mail.ru")
                .build();
        requestDto = RequestDto.builder()
                .id(1L)
                .description("desc")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.findRequestById(1L, 1L));
    }
}
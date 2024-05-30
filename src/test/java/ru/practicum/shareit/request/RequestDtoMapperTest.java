package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExtendedDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ExtendWith(MockitoExtension.class)
public class RequestDtoMapperTest {
    @Test
    void toOutgoingDtoTest() {
        Request request = Request.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .user(new User())
                .description("desc")
                .build();
        RequestExtendedDto expected = RequestExtendedDto.builder()
                .id(1L)
                .created(request.getCreated())
                .description("desc")
                .build();

        RequestExtendedDto actual = RequestMapper.toExtendedRequest(request);

        assertEquals(expected, actual);
    }

    @Test
    void toOutgoingDtoListTest() {
        Request request = Request.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .user(new User())
                .description("desc")
                .build();
        List<RequestExtendedDto> expected = List.of(
                RequestExtendedDto.builder()
                        .id(1L)
                        .created(request.getCreated())
                        .description("desc")
                        .build()
        );

        List<RequestExtendedDto> actual = List.of(RequestMapper.toExtendedRequest(request));

        assertIterableEquals(expected, actual);
    }

    @Test
    void toItemRequestTest() {
        RequestDto dto = RequestDto.builder()
                .description("desc")
                .build();
        Request expected = Request.builder()
                .description("desc")
                .build();

        Request actual = RequestMapper.toRequest(dto);

        assertEquals(expected, actual);
    }

    @Test
    void toItemRequestListTest() {
        RequestDto dto = RequestDto.builder()
                .description("desc")
                .build();
        List<Request> expected = List.of(
                Request.builder()
                        .description("desc")
                        .build()
        );

        List<Request> actual = List.of(RequestMapper.toRequest(dto));

        assertIterableEquals(expected, actual);
    }
}
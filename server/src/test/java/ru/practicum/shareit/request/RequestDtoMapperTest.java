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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RequestDtoMapperTest {
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
    void toItemRequestTest() {
        RequestDto dto = RequestDto.builder()
                .description("desc")
                .created(LocalDateTime.now())
                .build();
        Request expected = Request.builder()
                .description("desc")
                .created(dto.getCreated())
                .build();

        Request actual = RequestMapper.toRequest(dto);

        assertEquals(expected, actual);
    }
}
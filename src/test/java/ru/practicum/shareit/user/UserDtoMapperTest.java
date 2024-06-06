package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserDtoMapperTest {
    @Test
    void toDtoTest() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@maul.ru")
                .build();
        UserDto expected = UserDto.builder()
                .id(1L)
                .name(user.getName())
                .email(user.getEmail())
                .build();

        UserDto actual = UserMapper.toUserDto(user);

        assertEquals(expected, actual);
    }

    @Test
    void toUserTest() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@maul.ru")
                .build();
        User expected = User.builder()
                .id(1L)
                .name("name")
                .email("email@maul.ru")
                .build();

        User actual = UserMapper.toUser(dto);

        assertEquals(expected, actual);
    }
}
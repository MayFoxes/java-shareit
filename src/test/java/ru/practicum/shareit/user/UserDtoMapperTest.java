package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ExtendWith(MockitoExtension.class)
public class UserDtoMapperTest {
    @Test
    void toOutgoingDtoTest() {
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
    void toOutgoingDtoListTest() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@maul.ru")
                .build();
        List<UserDto> expected = List.of(
                UserDto.builder()
                        .id(1L)
                        .name(user.getName())
                        .email(user.getEmail())
                        .build()
        );

        List<UserDto> actual = Stream.of(user)
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        assertIterableEquals(expected, actual);
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

    @Test
    void toUseListTest() {
        List<UserDto> dtos = List.of(
                UserDto.builder()
                        .id(1L)
                        .name("name")
                        .email("email@maul.ru")
                        .build()
        );
        List<User> expected = List.of(
                User.builder()
                        .id(1L)
                        .name("name")
                        .email("email@maul.ru")
                        .build()
        );

        List<User> actual = dtos.stream()
                .map(UserMapper::toUser)
                .collect(Collectors.toList());

        assertIterableEquals(expected, actual);
    }
}
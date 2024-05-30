package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserServiceImpl userService;
    private final UserDto user = UserDto.builder()
            .id(1L)
            .name("name")
            .email("email@mail.ru")
            .build();

    @Test
    void createUserTest() {
        User result = userService.createUser(user);

        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void getUserTest() {
        User temUser = userService.createUser(user);

        UserDto actual = userService.findUserById(temUser.getId());

        assertEquals(UserMapper.toUserDto(temUser), actual);
    }

    @Test
    void getUserNotFoundTest() {
        assertThrows(NotFoundException.class, () -> userService.findUserById(1L));
    }

    @Test
    void updateUserTest() {
        User temUser = userService.createUser(user);

        UserDto forUpdate = UserDto.builder()
                .id(temUser.getId())
                .name("updated")
                .email(user.getEmail())
                .build();
        userService.updatedUser(temUser.getId(), forUpdate);

        assertEquals(forUpdate, userService.findUserById(temUser.getId()));
    }

    @Test
    void getAllTest() {
        userService.createUser(user);
        List<UserDto> expected = List.of(user);

        List<UserDto> actual = userService.findAllUsers();

        assertIterableEquals(expected, actual);
    }
}
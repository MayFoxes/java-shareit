package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User createUser(UserDto userDto);

    void deleteUser(Long userId);

    User updatedUser(Long id, UserDto userDto);

    UserDto findUserById(Long id);

    List<UserDto> findAllUsers();
}

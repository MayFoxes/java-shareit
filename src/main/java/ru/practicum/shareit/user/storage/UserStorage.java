package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    void deleteUser(Long userId);

    User updatedUser(Long id, User user);

    User findUserById(Long id);

    List<User> findAllUsers();
}

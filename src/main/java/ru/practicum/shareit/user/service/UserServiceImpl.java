package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    @Override
    public User updatedUser(Long id, User user) {
        return userStorage.updatedUser(id, user);
    }

    @Override
    public User findUserById(Long id) {
        return userStorage.findUserById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }
}

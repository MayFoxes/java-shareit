package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {

    private final HashMap<Long, User> userMap = new HashMap<>();
    private long userId = 0;

    @Override
    public User createUser(User user) {
        checkEmailExist(null, user);
        user.setId(++userId);
        userMap.put(userId, user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        checkUserExist(id);
        userMap.remove(id);
    }

    @Override
    public User updatedUser(Long id, User user) {
        checkUserExist(id);
        checkEmailExist(id, user);
        User savedUser = userMap.get(id);
        user = User.builder()
                .id(id)
                .name(user.getName() == null ? savedUser.getName() : user.getName())
                .email(user.getEmail() == null ? savedUser.getEmail() : user.getEmail()).build();
        userMap.put(id, user);
        return user;
    }

    @Override
    public User findUserById(Long id) {
        return userMap.get(id);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    private void checkUserExist(Long id) {
        if (userMap.get(id) == null) {
            throw new NotFoundException(String.format("No such user:%s with exist", id));
        }
    }

    private void checkEmailExist(Long id, User user) {
        if (user.getEmail() == null) {
            return;
        }
        if (userMap.values().stream()
                .filter(i -> !i.getId().equals(id))
                .anyMatch(e -> user.getEmail().equals(e.getEmail()))) {
            throw new AlreadyExistsException("Email already exist");
        }
    }
}

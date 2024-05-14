package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("A try to create a new user");
        return userService.createUser(user);
    }

    @DeleteMapping(path = "/{userId}")
    public void deleteUser(@Valid @PathVariable Long userId) {
        log.info("A try to delete a user with id:{}", userId);
        userService.deleteUser(userId);
    }

    @PatchMapping(path = "/{userId}")
    public User updateUser(@Valid @PathVariable Long userId, @RequestBody User user) {
        log.info("User with id:{} trying to update themself", userId);
        return userService.updatedUser(userId, user);
    }

    @GetMapping(path = "/{userId}")
    public User findUser(@Valid @PathVariable Long userId) {
        log.info("Searching user with id:{}", userId);
        return userService.findUserById(userId);
    }

    @GetMapping
    public List<User> findAllUsers() {
        log.info("Searching all users");
        return userService.findAllUsers();
    }
}

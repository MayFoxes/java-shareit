package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody UserDto userDto) {
        log.info("A try to create a new user");
        return userService.createUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Valid @PathVariable Long userId) {
        log.info("A try to delete a user with id:{}", userId);
        userService.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        log.info("User with id:{} trying to update themself", userId);
        return userService.updatedUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findUser(@Valid @PathVariable Long userId) {
        log.info("Searching user with id:{}", userId);
        return userService.findUserById(userId);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.info("Searching all users");
        return userService.findAllUsers();
    }
}

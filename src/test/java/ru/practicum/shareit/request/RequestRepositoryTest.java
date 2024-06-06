package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RequestRepositoryTest {
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    private static User user;
    private static Request request;

    @BeforeAll
    static void setUp() {
        user = User.builder()
                .id(1L)
                .name("username1")
                .email("user1@mail.ru")
                .build();
        request = Request.builder()
                .id(1L)
                .description("for item")
                .user(user)
                .created(LocalDateTime.now())
                .build();
    }

    @BeforeEach
    void save() {
        userRepository.save(user);
        requestRepository.save(request);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllByCreator() {
        Request received = requestRepository
                .findAllByUserId(user.getId(), Sort.by(Sort.Direction.ASC, "created"))
                .get(0);

        assertEquals(request.getId(), received.getId());
        assertEquals(request.getDescription(), received.getDescription());
        assertEquals(request.getUser(), received.getUser());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllByCreatorNotIn() {
        Request received = requestRepository
                .findAllByUserIdNot(2L, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id")))
                .get(0);

        assertEquals(request.getId(), received.getId());
        assertEquals(request.getDescription(), received.getDescription());
        assertEquals(request.getUser(), received.getUser());
    }
}
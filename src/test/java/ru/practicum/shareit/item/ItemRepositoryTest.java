package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;
    private static User user1;
    private static User user2;

    private static Request request;
    private static Item item;

    @BeforeAll
    static void setUp() {
        user2 = User.builder()
                .id(2L)
                .name("username2")
                .email("user2@mail.ru")
                .build();
        user1 = User.builder()
                .id(1L)
                .name("username1")
                .email("user1@mail.ru")
                .build();
        request = Request.builder()
                .id(1L)
                .description("for item")
                .user(user2)
                .created(LocalDateTime.now())
                .build();
        item = Item.builder()
                .id(1L)
                .name("itemname")
                .owner(user2.getId())
                .available(true)
                .description("description")
                .request(request)
                .build();
    }

    @BeforeEach
    void save() {
        userRepository.save(user1);
        userRepository.save(user2);
        requestRepository.save(request);
        itemRepository.save(item);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getByItemOwnerTest() {
        Item received = itemRepository.findByOwner(2L).get(0);

        assertEquals(item.getId(), received.getId());
        assertEquals(item.getName(), received.getName());
        assertEquals(item.getDescription(), received.getDescription());
        assertEquals(item.getAvailable(), received.getAvailable());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getByTextTest() {
        Item received = itemRepository.findByNameContainingIgnoreCase("item").get(0);

        assertEquals(item.getId(), received.getId());
        assertEquals(item.getName(), received.getName());
        assertEquals(item.getDescription(), received.getDescription());
        assertEquals(item.getAvailable(), received.getAvailable());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getByRequestTest() {
        Item received = itemRepository.findAllByRequest(request).get(0);

        assertEquals(item.getId(), received.getId());
        assertEquals(item.getName(), received.getName());
        assertEquals(item.getDescription(), received.getDescription());
        assertEquals(item.getAvailable(), received.getAvailable());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getByRequestListTest() {
        Item received = itemRepository.findAllByRequest(request).get(0);

        assertEquals(item.getName(), received.getName());
        assertEquals(item.getDescription(), received.getDescription());
        assertEquals(item.getAvailable(), received.getAvailable());
    }
}
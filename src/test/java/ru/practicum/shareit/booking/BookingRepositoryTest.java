package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private final User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@yandex.ru")
            .build();

    private final User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@yandex.ru")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .owner(2L)
            .request(null)
            .build();

    private final Item item2 = Item.builder()
            .id(2L)
            .name("item2")
            .description("description2")
            .available(true)
            .owner(2L)
            .request(null)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1))
            .item(item2)
            .booker(user2)
            .status(BookingStatus.APPROVED)
            .build();

    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @BeforeEach
    void setUp() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item);
        itemRepository.save(item2);
        bookingRepository.save(booking);
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllByBookerTest() {
        Booking received = bookingRepository.findAllByBookerId(user2.getId(), sort).get(0);

        assertEquals(booking.getId(), received.getId());
        assertEquals(booking.getStart(), received.getStart());
        assertEquals(booking.getEnd(), received.getEnd());
        assertEquals(booking.getItem(), received.getItem());
        assertEquals(booking.getBooker(), received.getBooker());
        assertEquals(booking.getStatus(), received.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllByBookerAndStatusTest() {
        Booking received = bookingRepository.findAllByBookerIdAndStatus(user2.getId(), BookingStatus.APPROVED, sort).get(0);

        assertEquals(booking.getId(), received.getId());
        assertEquals(booking.getStart(), received.getStart());
        assertEquals(booking.getEnd(), received.getEnd());
        assertEquals(booking.getItem(), received.getItem());
        assertEquals(booking.getBooker(), received.getBooker());
        assertEquals(booking.getStatus(), received.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllByItemOwnerTest() {
        Booking received = bookingRepository.findAllByItemOwner(user2.getId(), sort).get(0);

        assertEquals(booking.getId(), received.getId());
        assertEquals(booking.getStart(), received.getStart());
        assertEquals(booking.getEnd(), received.getEnd());
        assertEquals(booking.getItem(), received.getItem());
        assertEquals(booking.getBooker(), received.getBooker());
        assertEquals(booking.getStatus(), received.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllByItemOwnerAndStatusTest() {
        Booking received = bookingRepository.findAllByItemOwnerAndStatus(user2.getId(), BookingStatus.APPROVED, sort).get(0);

        assertEquals(booking.getId(), received.getId());
        assertEquals(booking.getStart(), received.getStart());
        assertEquals(booking.getEnd(), received.getEnd());
        assertEquals(booking.getItem(), received.getItem());
        assertEquals(booking.getBooker(), received.getBooker());
        assertEquals(booking.getStatus(), received.getStatus());
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void getAllByItemsListTest() {
        Booking received = bookingRepository.findAllByItemIn(List.of(item2), sort).get(0);

        assertEquals(booking.getId(), received.getId());
        assertEquals(booking.getStart(), received.getStart());
        assertEquals(booking.getEnd(), received.getEnd());
        assertEquals(booking.getItem(), received.getItem());
        assertEquals(booking.getBooker(), received.getBooker());
        assertEquals(booking.getStatus(), received.getStatus());
    }
}
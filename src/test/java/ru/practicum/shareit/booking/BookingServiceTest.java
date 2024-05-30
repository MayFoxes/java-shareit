package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private final User user1 = User.builder()
            .id(1L)
            .name("User1")
            .email("user1@example.com")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("User2")
            .email("user2@example.com")
            .build();
    private final Item item1 = Item.builder()
            .id(1L)
            .name("Item1")
            .description("Description1")
            .available(true)
            .owner(user1.getId())
            .request(null)
            .build();
    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .status(BookingStatus.WAITING)
                .booker(user2)
                .item(item1)
                .build();
    }

    @Test
    void getBookingByBookerTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingExtendedDto result = bookingService.getBookingById(1L, 2L);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingByItemOwnerTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingExtendedDto result = bookingService.getBookingById(1L, 1L);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }
}
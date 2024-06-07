package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {
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
            .owner(user1)
            .request(null)
            .build();
    private Booking booking;
    private BookingDto bookingDto;

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

        bookingDto = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .booker(user2.getId())
                .itemId(item1.getId())
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

    @Test
    void getBookingNoAccessTest() {
        User user = User.builder()
                .id(3L)
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 3L));
    }

    @Test
    void getBookingsOwnerNotFoundTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));

        assertThrows(NotFoundException.class, () -> bookingService.getItemOwnerBookings(1L, BookingState.CURRENT, 0, 10));
    }

    @Test
    void rejectBookingAlreadyApproveTest() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        assertThrows(ValidationException.class, () -> bookingService.approveOrRejectBooking(1L, 1L, false));
    }

    @Test
    void rejectBookingNotOwnerTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));

        assertThrows(NotFoundException.class,
                () -> bookingService.approveOrRejectBooking(1L, 99L, false));
    }

    @Test
    void createBookingNotFoundItemTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));
        BookingDto forCreate = bookingDto;
        bookingDto.setItemId(Item.builder()
                .id(3L)
                .build().getId());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(forCreate, 2L)
        );
        assertEquals("Item:3 is not exist.", e.getMessage());
    }

    @Test
    void approveBookingNotOwnerTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));

        assertThrows(NotFoundException.class,
                () -> bookingService.approveOrRejectBooking(1L, 99L, true));
    }

    @Test
    void approveBookingTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenReturn(Booking.builder()
                        .id(booking.getId())
                        .start(booking.getStart())
                        .end(booking.getEnd())
                        .booker(booking.getBooker())
                        .item(booking.getItem())
                        .status(BookingStatus.APPROVED)
                        .build());

        Booking result = bookingService.approveOrRejectBooking(booking.getId(), user1.getId(), true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void deleteBookingByIdTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        bookingRepository.deleteById(1L);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .deleteById(anyLong());
    }

    @Test
    void approveBookingAlreadyApproveTest() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        assertThrows(ValidationException.class, () -> bookingService.approveOrRejectBooking(1L, 1L, true));
    }

    @Test
    void createBookingNotFoundBookerTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .booker(user2.getId())
                .itemId(item1.getId())
                .build();

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, 1L));
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        BookingExtendedDto result = bookingService.createBooking(bookingDto, 2L);
        BookingExtendedDto bookingExtendedDto = BookingMapper.toBookingExtendedDto(BookingMapper.toBooking(bookingDto));

        assertEquals(bookingExtendedDto.getId(), result.getId());
        assertEquals(bookingExtendedDto.getStart(), result.getStart());
        assertEquals(bookingExtendedDto.getEnd(), result.getEnd());
        assertEquals(bookingExtendedDto.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsBookerNotFoundTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));

        assertThrows(NotFoundException.class, () -> bookingService.getUserBookings(1L, BookingState.CURRENT, 0, 10));
    }

    @Test
    void createBookingNotAvailableItemTest() {
        Item notAvailableItem = Item.builder()
                .id(5L)
                .available(false)
                .owner(user1)
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(notAvailableItem));
        BookingDto forCreate = bookingDto;
        forCreate.setItemId(notAvailableItem.getId());

        ValidationException e = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(forCreate, 2L)
        );
        assertEquals("Item:5 is unavailable.", e.getMessage());
    }

    @Test
    void rejectBookingTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        Booking result = bookingService.approveOrRejectBooking(booking.getId(), user1.getId(), false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void getBookingsOfItemOwnerTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByItemOwner(any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        BookingExtendedDto result = bookingService.getItemOwnerBookings(1L, BookingState.ALL, 0, 10).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsOfItemOwnerByCurrentStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        booking.setStart(LocalDateTime.now().minusHours(1));
        when(bookingRepository.findAllByItemOwner(any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        BookingExtendedDto result = bookingService
                .getItemOwnerBookings(1L, BookingState.CURRENT, 0, 10).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsOfBookerByWaitingState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBookerAndStatus(any(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        BookingExtendedDto result = bookingService
                .getUserBookings(2L, BookingState.WAITING, 0, 10).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsOfBookerTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBooker(any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        BookingExtendedDto result = bookingService
                .getUserBookings(2L, BookingState.ALL, 0, 10).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }

    @Test
    void getBookingsOfItemOwnerByWaitingStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByItemOwnerAndStatus(any(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        BookingExtendedDto result = bookingService
                .getItemOwnerBookings(1L, BookingState.WAITING, 0, 10).get(0);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(user2, result.getBooker());
        assertEquals(item1, result.getItem());
    }
}
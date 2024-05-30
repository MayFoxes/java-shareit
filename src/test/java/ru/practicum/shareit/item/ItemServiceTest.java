package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;
    private Item item;
    private User user;

    @Test
    void getItemByIdTest() {
        user = User.builder()
                .id(1L)
                .name("username")
                .email("asd@mail.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("itemname")
                .description("description")
                .available(true)
                .owner(user.getId())
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemExtendedDto expected = ItemMapper.toItemDto(item);
        expected.setComments(List.of());

        ItemExtendedDto actual = itemService.findItemById(item.getId(), user.getId());

        assertEquals(expected, actual);
    }

    @Test
    void updateItemTest() {
        user = User.builder()
                .id(1L)
                .name("username")
                .email("asd@mail.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("itemname")
                .description("description")
                .available(true)
                .owner(user.getId())
                .build();
        ItemDto updated = ItemDto.builder()
                .id(1L)
                .name("updated")
                .description("desc")
                .available(false)
                .owner(user.getId())
                .build();
        when(itemRepository.save(ItemMapper.toItem(updated)))
                .thenReturn(ItemMapper.toItem(updated));

        ItemExtendedDto itemExtendedDtoTest = ItemMapper.toItemDto(item);

        assertEquals(ItemMapper.toItem(updated), ItemMapper.toUpdatedItem(user.getId(), itemExtendedDtoTest, updated));
    }

    @Test
    void getAllByOwnerTest() {
        user = User.builder()
                .id(1L)
                .name("username")
                .email("asd@mail.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("itemname")
                .description("description")
                .available(true)
                .owner(user.getId())
                .build();
        User booker = User.builder()
                .id(2L)
                .name("booker")
                .email("booker@mail.ru")
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        when(itemRepository.findByOwner(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIn(any(), (Pageable) any()))
                .thenReturn(List.of(booking));

        List<ItemExtendedDto> expected = List.of(ItemMapper.toItemDto(item));
        expected.get(0).setComments(List.of());
        expected.get(0).setLastBooking(BookingMapper.toBookingItemDto(booking));

        List<ItemExtendedDto> actual = itemService.findItemsByOwner(1L, 0, 1);

        assertIterableEquals(expected, actual);
    }

    @Test
    void getByText() {
        item = Item.builder()
                .id(1L)
                .name("itemname")
                .description("description")
                .available(true)
                .owner(User.builder()
                        .id(1L)
                        .name("name")
                        .email("asd@mail.com")
                        .build().getId())
                .build();
        when(itemRepository.findByNameContainingIgnoreCase(any(String.class)))
                .thenReturn(List.of(item));
        List<Item> expected = List.of(item);

        List<ItemExtendedDto> actual = itemService.findItemsByName("ite", 0, 1);

        assertIterableEquals(expected.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()), actual);
    }
}
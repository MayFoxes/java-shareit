package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceTest {

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
    private ItemDto itemDto;
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

    @Test
    void getItemNotFoundTest() {
        item = Item.builder()
                .id(1L)
                .name("itemname")
                .description("description")
                .available(true)
                .build();
        user = User.builder()
                .id(1L)
                .name("username")
                .email("asd@mail.ru")
                .build();
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findItemById(1L, 1L));
    }

    @Test
    void createItemUserNotFoundTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        user = User.builder()
                .id(1L)
                .name("username")
                .email("asd@mail.ru")
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("itemname")
                .description("description")
                .available(true)
                .owner(user.getId())
                .build();

        assertThrows(NotFoundException.class, () -> itemService.saveItem(99L, itemDto));
    }

    @Test
    void getItemByIdNotFoundTest() {
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
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findItemById(user.getId(), item.getId() + 1));
    }

    @Test
    void updateItemNoAccessTest() {
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

        assertThrows(NotFoundException.class, () -> itemService.updateItem(user.getId() + 1, item.getId(), updated));
    }

    @Test
    void createCommentUserNotFoundTest() {
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
                .owner(User.builder()
                        .id(2L)
                        .name("user2")
                        .email("email2@mail.ru")
                        .build().getId())
                .build();
        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("good")
                .created(LocalDateTime.now())
                .user(user.getId())
                .item(item.getId())
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now())
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findAllByBookerId(any(Long.class), any(Sort.class)))
                .thenReturn(List.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.save(CommentMapper.toComment(comment)))
                .thenReturn(CommentMapper.toComment(comment));

        assertThrows(
                NotFoundException.class, () -> itemService.createComment(comment, item.getId(), comment.getUser())
        );
    }

    @Test
    void createCommentTest() {
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
                .owner(User.builder()
                        .id(2L)
                        .name("user2")
                        .email("email2@mail.ru")
                        .build().getId()).build();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("good")
                .created(LocalDateTime.now())
                .user(user.getId())
                .item(item.getId()).build();
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setUser(user);
        comment.setAuthorName(user.getName());
        comment.setItem(item);
        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now())
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerId(any(Long.class), any(Sort.class)))
                .thenReturn(List.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.save(comment))
                .thenReturn(comment);

        assertEquals(CommentMapper.toCommentDto(comment), itemService.createComment(commentDto, item.getId(), commentDto.getUser()));
    }

    @Test
    void getItemTest() {
        item = Item.builder()
                .id(1L)
                .name("itemname")
                .description("description")
                .available(true)
                .owner(1L)
                .build();
        ItemExtendedDto tempItem = ItemMapper.toItemDto(item);
        tempItem.setComments(new ArrayList<>());
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemExtendedDto actual = itemService.findItemById(1L, 1L);

        assertEquals(tempItem, actual);
    }

    @Test
    void createItemTest() {
        user = User.builder()
                .id(1L)
                .name("username")
                .email("asd@mail.ru")
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("itemname")
                .description("description")
                .available(true)
                .owner(user.getId()).build();
        item = ItemMapper.toItem(itemDto);
        when(itemRepository.save(item))
                .thenReturn(item);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Item expected = Item.builder()
                .id(1L)
                .name("itemname")
                .description("description")
                .available(true)
                .owner(user.getId()).build();

        assertEquals(ItemMapper.toItemDto(expected), itemService.saveItem(user.getId(), itemDto));
    }
}
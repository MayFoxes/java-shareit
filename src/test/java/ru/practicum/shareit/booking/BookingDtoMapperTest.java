package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ExtendWith(MockitoExtension.class)
public class BookingDtoMapperTest {
    @Test
    void toOutgoingDtoTest() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();
        BookingExtendedDto expectedDto = BookingExtendedDto.builder()
                .id(1L)
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();

        BookingExtendedDto actualDto = BookingMapper.toBookingExtendedDto(booking);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void toBookingTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        Booking actualBooking = BookingMapper.toBooking(bookingDto);

        assertEquals(bookingDto.getStart(), actualBooking.getStart());
        assertEquals(bookingDto.getEnd(), actualBooking.getEnd());
    }

    @Test
    void toBookingForItemDtoTest() {
        User booker = User.builder()
                .id(1L)
                .name("name")
                .email("ilya@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("description")
                .owner(1L)
                .available(true)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        BookingExtendedDto expectedDto = BookingExtendedDto.builder()
                .id(1L)
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        BookingExtendedDto actualDto = BookingMapper.toBookingExtendedDto(booking);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void toOutgoingDtoListTest() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.REJECTED)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);
        BookingExtendedDto expectedDto1 = BookingExtendedDto.builder()
                .id(1L)
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();
        BookingExtendedDto expectedDto2 = BookingExtendedDto.builder()
                .id(2L)
                .start(booking2.getStart())
                .end(booking2.getEnd())
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.REJECTED)
                .build();
        List<BookingExtendedDto> expectedDtos = List.of(expectedDto1, expectedDto2);

        List<BookingExtendedDto> actualDtos = bookings.stream()
                .map(BookingMapper::toBookingExtendedDto)
                .collect(Collectors.toList());

        assertIterableEquals(expectedDtos, actualDtos);
    }

    @Test
    void toBookingListTest() {
        BookingDto dto1 = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();
        BookingDto dto2 = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .build();
        Booking expectedBooking1 = Booking.builder()
                .start(dto1.getStart())
                .end(dto1.getEnd())
                .status(BookingStatus.WAITING)
                .build();
        Booking expectedBooking2 = Booking.builder()
                .start(dto2.getStart())
                .end(dto2.getEnd())
                .status(BookingStatus.WAITING)
                .build();
        List<BookingDto> dtos = List.of(dto1, dto2);

        List<Booking> expectedBookings = List.of(expectedBooking1, expectedBooking2);

        List<Booking> actualBookings = dtos.stream()
                .map(BookingMapper::toBooking)
                .collect(Collectors.toList());

        assertIterableEquals(expectedBookings, actualBookings);
    }

    @Test
    void toBookingForItemListTest() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .item(
                        Item.builder()
                                .id(1L)
                                .build()
                )
                .booker(
                        User.builder()
                                .id(1L)
                                .build()
                )
                .status(BookingStatus.APPROVED)
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .item(
                        Item.builder()
                                .id(2L)
                                .build()
                )
                .booker(
                        User.builder()
                                .id(2L)
                                .build()
                )
                .status(BookingStatus.REJECTED)
                .build();
        List<Booking> bookings = List.of(booking1, booking2);

        BookingExtendedDto expectedDto1 = BookingExtendedDto.builder()
                .id(1L)
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .item(Item.builder()
                        .id(1L)
                        .build())
                .booker(User.builder()
                        .id(1L)
                        .build())
                .status(BookingStatus.APPROVED)
                .build();
        BookingExtendedDto expectedDto2 = BookingExtendedDto.builder()
                .id(2L)
                .start(booking2.getStart())
                .end(booking2.getEnd())
                .item(Item.builder()
                        .id(2L)
                        .build())
                .booker(User.builder()
                        .id(2L)
                        .build())
                .status(BookingStatus.REJECTED)
                .build();
        List<BookingExtendedDto> expectedDtos = List.of(expectedDto1, expectedDto2);

        List<BookingExtendedDto> actualDtos = bookings.stream()
                .map(BookingMapper::toBookingExtendedDto)
                .collect(Collectors.toList());

        assertIterableEquals(expectedDtos, actualDtos);
    }
}
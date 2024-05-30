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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BookingDtoMapperTest {
    @Test
    void toExtendedDtoTest() {
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
    void toBookingItemDtoTest() {
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
}
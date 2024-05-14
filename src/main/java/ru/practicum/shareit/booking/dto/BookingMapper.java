package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ValidationException;

public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto) {
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart()))
            throw new ValidationException("The end should be after start.");

        return Booking.builder()
                .status(BookingStatus.WAITING)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public static BookingExtendedDto toBookingExtendedDto(Booking booking) {
        return BookingExtendedDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .status(booking.getStatus())
                .build();
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        return BookingItemDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .status(booking.getStatus())
                .end(booking.getEnd())
                .build();
    }
}
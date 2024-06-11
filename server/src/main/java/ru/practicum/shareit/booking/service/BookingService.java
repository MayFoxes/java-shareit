package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingExtendedDto createBooking(BookingDto bookingDto, Long owner);

    BookingExtendedDto getBookingById(Long bookingId, Long userId);

    List<BookingExtendedDto> getUserBookings(Long userId, BookingState state, Integer from, Integer size);

    List<BookingExtendedDto> getItemOwnerBookings(Long ownerId, BookingState state, Integer from, Integer size);

    Booking approveOrRejectBooking(Long bookingId, Long itemOwnerId, Boolean approved);
}

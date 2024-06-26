package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UnsupportedStateException;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingExtendedDto createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @RequestBody BookingDto bookingDto) {
        log.info("Request to create booking.");
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking approveBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @PathVariable Long bookingId,
                                  @RequestParam boolean approved) {
        log.info("Request to approve booking.");
        return bookingService.approveOrRejectBooking(bookingId, userId, approved);
    }


    @GetMapping("/{bookingId}")
    public BookingExtendedDto findBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @PathVariable Long bookingId) {
        log.info("Request to receive booking:{}.", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingExtendedDto> findUserBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam Integer from,
                                                     @RequestParam Integer size,
                                                     @RequestParam BookingState state) {
        log.info("Request to receive user:{} bookings.", userId);
        try {
            return bookingService.getUserBookings(userId, state, from, size);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: " + state);
        }
    }

    @GetMapping("/owner")
    public List<BookingExtendedDto> findByItemOwnerBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                            @RequestParam Integer from,
                                                            @RequestParam Integer size,
                                                            @RequestParam BookingState state) {
        log.info("Request to receive item owner:{} bookings.", userId);
        try {
            return bookingService.getItemOwnerBookings(userId, state, from, size);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: " + state);
        }
    }
}

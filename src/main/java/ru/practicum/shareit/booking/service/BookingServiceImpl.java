package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingExtendedDto createBooking(BookingDto bookingDto, Long owner) {
        User user = getUserById(owner);
        Item item = getItemById(bookingDto.getItemId());

        if (item.getOwner().equals(owner)) {
            throw new NotFoundException("Booker can not be an item owner.");
        }
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Item:%d is unavailable.", item.getId()));
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);

        return BookingMapper.toBookingExtendedDto(bookingRepository.save(booking));
    }

    @Override
    public BookingExtendedDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking:%d is not found.", bookingId)));
        boolean isUserBooker = booking.getBooker().getId().equals(userId);
        boolean isUserItemOwner = booking.getItem().getOwner().equals(userId);

        if (!isUserBooker && !isUserItemOwner) {
            throw new NotFoundException(
                    String.format("User:%d is not a booker Or a owner of booking:%d.", userId, bookingId));
        }

        return BookingMapper.toBookingExtendedDto(booking);
    }

    @Override
    public List<BookingExtendedDto> getUserBookings(Long userId, BookingState state, Integer from, Integer size) {
        getUserById(userId);
        Pagination pagination = new Pagination(from, size);
        Pageable page = pagination.getPageable();

        return getBookingsByStatus(userId, state, page).stream()
                .map(BookingMapper::toBookingExtendedDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingExtendedDto> getItemOwnerBookings(Long ownerId, BookingState state, Integer from, Integer size) {
        getUserById(ownerId);
        Pagination pagination = new Pagination(from, size);
        Pageable page = pagination.getPageable();

        return getOwnerBookingsByStatus(ownerId, state, page).stream()
                .map(BookingMapper::toBookingExtendedDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Booking approveOrRejectBooking(Long bookingId, Long itemOwnerId, Boolean approved) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getItem().getOwner().equals(itemOwnerId)) {
            throw new NotFoundException("Booker can not be an item owner.");
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException(String.format("Booking %d is already approved.", bookingId));
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }

    private User getUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User:%d does not exist.", userId)));
    }

    private Item getItemById(Long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item:%d is not exist.", itemId)));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository
                .findById(bookingId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Booking:%d is not found.", bookingId)));
    }

    private Predicate<Booking> getFilterByState(BookingState state) {
        switch (state) {
            case PAST:
                return booking -> booking.getEnd().isBefore(LocalDateTime.now());
            case CURRENT:
                return booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
                        booking.getEnd().isAfter(LocalDateTime.now());
            case FUTURE:
                return booking -> booking.getStart().isAfter(LocalDateTime.now());
            default:
                throw new ValidationException("Provided wrong state of booking.");
        }
    }

    private List<Booking> getBookingsByStatus(Long userId, BookingState state, Pageable page) {
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = new ArrayList<>(bookingRepository.findAllByBookerId(userId, page));
                break;
            case WAITING:
            case REJECTED:
            case CANCELLED:
                BookingStatus status = BookingStatus.valueOf(state.name());
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndStatus(userId, status, page));
                break;
            case PAST:
            case CURRENT:
            case FUTURE:
                bookings = bookingRepository.findAllByBookerId(userId, page).stream()
                        .filter(getFilterByState(state))
                        .collect(Collectors.toList());
        }
        return bookings;
    }

    private List<Booking> getOwnerBookingsByStatus(Long ownerId, BookingState state, Pageable page) {
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwner(ownerId, page));
                break;
            case WAITING:
            case REJECTED:
            case CANCELLED:
                BookingStatus status = BookingStatus.valueOf(state.name());
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerAndStatus(ownerId, status, page));
                break;
            case PAST:
            case CURRENT:
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwner(ownerId, page).stream()
                        .filter(getFilterByState(state))
                        .collect(Collectors.toList());
        }
        return bookings;
    }
}

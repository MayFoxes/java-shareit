package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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

        if (item.getOwner().equals(owner))
            throw new NotFoundException("Booker can not be an item owner.");
        if (!item.getAvailable())
            throw new ValidationException(String.format("Item:%d is unavailable.", item.getId()));

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);

        return BookingMapper.toBookingExtendedDto(bookingRepository.save(booking));
    }

    @Override
    public BookingExtendedDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking:%d is not found.", bookingId)));
        boolean isUserBooker = booking.getBooker().getId().equals(userId);
        boolean isUserItemOwner = booking.getItem().getOwner().equals(userId);

        if (!isUserBooker && !isUserItemOwner)
            throw new NotFoundException(String.format("User:%d is not a booker Or a owner of booking:%d.", userId, bookingId));

        return BookingMapper.toBookingExtendedDto(booking);
    }

    @Override
    public List<BookingExtendedDto> getUserBookings(Long userId, BookingState state) {
        getUserById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;

        if (state.equals(BookingState.ALL)) {
            bookings = new ArrayList<>(bookingRepository.findAllByBookerId(userId, sort));
        } else {
            try {
                BookingStatus status = BookingStatus.valueOf(state.name());
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, status, sort);
            } catch (IllegalArgumentException e) {
                bookings = bookingRepository.findAllByBookerId(userId, sort).stream()
                        .filter(getFilterByState(state))
                        .collect(Collectors.toList());
            }
        }

        return bookings.stream()
                .map(BookingMapper::toBookingExtendedDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingExtendedDto> getItemOwnerBookings(Long ownerId, BookingState state) {
        User owner = getUserById(ownerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;

        if (state.equals(BookingState.ALL)) {
            bookings = new ArrayList<>(bookingRepository.findAllByItemOwner(ownerId, sort));
        } else {
            try {
                BookingStatus status = BookingStatus.valueOf(state.name());
                bookings = bookingRepository.findAllByItemOwnerAndStatus(ownerId, status, sort);
            } catch (IllegalArgumentException e) {
                bookings = bookingRepository.findAllByItemOwner(ownerId, sort).stream()
                        .filter(getFilterByState(state))
                        .collect(Collectors.toList());
            }
        }

        return bookings.stream()
                .map(BookingMapper::toBookingExtendedDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Booking approveOrRejectBooking(Long bookingId, Long itemOwnerId, Boolean approved) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getItem().getOwner().equals(itemOwnerId))
            throw new NotFoundException("Booker can not be an item owner.");
        if (booking.getStatus().equals(BookingStatus.APPROVED))
            throw new ValidationException(String.format("Booking %d is already approved.", bookingId));

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return bookingRepository.save(booking);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User:%d does not exist.", userId)));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item:%d is not exist.", itemId)));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking:%d is not found.", bookingId)));
    }

    private Predicate<Booking> getFilterByState(BookingState state) {
        switch (state) {
            case PAST:
                return booking ->
                        booking.getEnd().isBefore(LocalDateTime.now());
            case CURRENT:
                return booking ->
                        booking.getStart().isBefore(LocalDateTime.now()) && booking.getEnd().isAfter(LocalDateTime.now());
            case FUTURE:
                return booking ->
                        booking.getStart().isAfter(LocalDateTime.now());
            default:
                throw new ValidationException("Provided wrong state of booking.");
        }
    }
}

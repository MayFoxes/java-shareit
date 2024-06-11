package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker(User booker, Pageable page);

    List<Booking> findAllByBooker(User booker, Sort sort);

    List<Booking> findAllByBookerAndStatus(User booker, BookingStatus status, Pageable page);

    List<Booking> findAllByBookerAndStatus(User booker, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwner(User itemOwner, Pageable page);

    List<Booking> findAllByItemOwner(User itemOwner, Sort sort);

    List<Booking> findAllByItemOwnerAndStatus(User itemOwner, BookingStatus status, Pageable page);

    List<Booking> findAllByItemOwnerAndStatus(User itemOwner, BookingStatus status, Sort sort);

    List<Booking> findAllByItemIn(List<Item> items, Pageable pageable);

    List<Booking> findAllByItemIn(List<Item> items, Sort sort);
}
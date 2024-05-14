package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long booker, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long booker, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwner(Long itemOwner, Sort sort);

    List<Booking> findAllByItemOwnerAndStatus(Long itemOwner, BookingStatus status, Sort sort);

    List<Booking> findAllByItemIn(List<Item> items, Sort sort);
}

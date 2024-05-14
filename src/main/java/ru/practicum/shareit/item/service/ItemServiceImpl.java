package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentExtendedDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public Item saveItem(Long ownerId, ItemDto itemDto) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User does not exist"));

        itemDto.setOwner(ownerId);
        return itemRepository.save(ItemMapper.toItem(itemDto));
    }

    @Override
    public ItemExtendedDto findItemById(Long ownerId, Long itemId) {
        Item tempItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item:%d does not exist.", itemId)));

        ItemExtendedDto tempDto = ItemMapper.toItemDto(tempItem);
        tempDto.setComments(commentRepository.findAllByItem(tempItem));

        if (tempItem.getOwner().equals(ownerId)) {
            Sort sort = Sort.by(Sort.Direction.ASC, "start");
            List<BookingItemDto> bookings = bookingRepository.findAllByItemIn(List.of(tempItem), sort).stream()
                    .map(BookingMapper::toBookingItemDto)
                    .collect(Collectors.toList());
            tempDto.setLastBooking(getLastBooking(bookings));
            tempDto.setNextBooking(getNextBooking(bookings));
        }
        return tempDto;
    }

    @Override
    public List<ItemExtendedDto> findItemsByName(String name) {
        if (name.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findByNameContainingIgnoreCase(name).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemExtendedDto> findItemsByOwner(Long ownerId) {
        List<Item> tempItems = itemRepository.findByOwner(ownerId);
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        List<Booking> tempBookings = bookingRepository.findAllByItemIn(tempItems, sort);
        Map<Item, List<Booking>> bookingsByItems = new HashMap<>();

        for (Item item : tempItems) {
            List<Booking> itemBookings = tempBookings.stream()
                    .filter(booking -> booking.getItem().equals(item))
                    .collect(Collectors.toList());
            bookingsByItems.put(item, itemBookings);
        }

        List<ItemExtendedDto> itemsDto = new ArrayList<>();

        for (Item item : tempItems) {
            ItemExtendedDto dtoItem = ItemMapper.toItemDto(item);

            dtoItem.setComments(commentRepository.findAllByItem(item));

            if (item.getOwner().equals(ownerId)) {
                List<Booking> bookings = bookingsByItems.get(item);
                List<BookingItemDto> outForItemBookings = bookings.stream()
                        .map(BookingMapper::toBookingItemDto)
                        .collect(Collectors.toList());
                dtoItem.setLastBooking(getLastBooking(outForItemBookings));
                dtoItem.setNextBooking(getNextBooking(outForItemBookings));
            }
            itemsDto.add(dtoItem);
        }
        return itemsDto;
    }

    @Transactional
    @Override
    public ItemExtendedDto updateItem(Long ownerId, Long itemId, ItemDto changes) {
        if (findItemsByOwner(ownerId).stream()
                .noneMatch(i -> i.getId().equals(itemId)))
            throw new NotFoundException("Only owner can change this");

        ItemExtendedDto tempDto = findItemById(ownerId, itemId);
        Item tempItem = ItemMapper.toUpdatedItem(ownerId, tempDto, changes);
        tempItem.setOwner(ownerId);

        return ItemMapper.toItemDto(itemRepository.save(tempItem));
    }

    @Transactional
    @Override
    public CommentExtendedDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        commentDto.setCreated(LocalDateTime.now());
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User %d is not found.", userId)));

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        bookingRepository.findAllByBookerId(userId, sort).stream()
                .filter(booking -> booking.getItem().getId().equals(itemId) &&
                        booking.getBooker().getId().equals(userId) &&
                        booking.getEnd().isBefore(LocalDateTime.now()) &&
                        booking.getStatus().equals(BookingStatus.APPROVED))
                .findFirst()
                .orElseThrow(() -> new ValidationException(String.format("User:%d can not leave a comment for Item:%d.", userId, itemId)));

        Comment tempComment = CommentMapper.toComment(commentDto);
        tempComment.setUser(user);
        tempComment.setItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item:%d does not exist.", itemId))));
        tempComment.setAuthorName(user.getName());

        return CommentMapper.toCommentDto(commentRepository.save(tempComment));
    }


    private BookingItemDto getLastBooking(List<BookingItemDto> bookingItemsDto) {
        List<BookingItemDto> allPastBookings = bookingItemsDto.stream()
                .filter(dto -> dto.getStart().isBefore(LocalDateTime.now()) &&
                        dto.getStatus().equals(BookingStatus.APPROVED))
                .collect(Collectors.toList());
        if (allPastBookings.isEmpty()) {
            return null;
        }
        return allPastBookings.get(allPastBookings.size() - 1);
    }

    private BookingItemDto getNextBooking(List<BookingItemDto> bookingItemsDto) {
        bookingItemsDto = bookingItemsDto.stream()
                .filter(dtoBooking -> dtoBooking.getStart().isAfter(LocalDateTime.now()) &&
                        dtoBooking.getStatus().equals(BookingStatus.APPROVED))
                .collect(Collectors.toList());
        if (bookingItemsDto.isEmpty()) {
            return null;
        }
        return bookingItemsDto.get(0);
    }
}

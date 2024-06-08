package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.Pagination;
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
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public ItemExtendedDto saveItem(Long ownerId, ItemDto itemDto) {
        if (itemDto.getRequestId() != null) {
            checkRequestExist(itemDto.getRequestId());
        }

        Item savedItem = ItemMapper.toItem(itemDto);
        savedItem = addRequestItem(itemDto.getRequestId(), savedItem);
        savedItem.setOwner(getUserById(ownerId));

        return ItemMapper.toItemDto(itemRepository.save(savedItem));
    }

    @Override
    public ItemExtendedDto findItemById(Long ownerId, Long itemId) {
        Item tempItem = itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item:%d does not exist.", itemId)));

        ItemExtendedDto tempDto = ItemMapper.toItemDto(tempItem);
        tempDto.setComments(commentRepository.findAllByItem(tempItem));

        if (tempItem.getOwner().getId().equals(ownerId)) {
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
    public List<ItemExtendedDto> findItemsByName(String name, Integer from, Integer size) {
        if (name.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.findByNameContainingIgnoreCase(name).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemExtendedDto> findItemsByOwner(Long ownerId, Integer from, Integer size) {
        List<Item> tempItems = itemRepository.findAllByOwnerOrderByOwner(getUserById(ownerId));
        Pagination pagination = new Pagination(from, size, Sort.by(Sort.Direction.DESC, "id"));
        Pageable page = pagination.getPageable();

        List<Booking> tempBookings = bookingRepository.findAllByItemIn(tempItems, page);
        Map<Long, List<Booking>> bookingsByItems = new HashMap<>();

        for (Item item : tempItems) {
            List<Booking> itemBookings = tempBookings.stream()
                    .filter(booking -> booking.getItem().equals(item))
                    .collect(Collectors.toList());
            bookingsByItems.put(item.getId(), itemBookings);
        }

        List<ItemExtendedDto> itemsDto = new ArrayList<>();

        for (Item item : tempItems) {
            ItemExtendedDto dtoItem = ItemMapper.toItemDto(item);

            dtoItem.setComments(commentRepository.findAllByItem(item));

            if (item.getOwner().getId().equals(ownerId)) {
                List<Booking> bookings = bookingsByItems.get(item.getId());
                List<BookingItemDto> outForItemBookings = bookings.stream()
                        .map(BookingMapper::toBookingItemDto)
                        .collect(Collectors.toList());
                dtoItem.setLastBooking(getLastBooking(outForItemBookings));
                dtoItem.setNextBooking(getNextBooking(outForItemBookings));
                itemsDto.add(dtoItem);
            }
        }
        return itemsDto.stream().sorted(Comparator.comparing(ItemExtendedDto::getId))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemExtendedDto updateItem(Long ownerId, Long itemId, ItemDto changes) {
        if (findItemsByOwner(ownerId, 0, 1).stream().noneMatch(i -> i.getId().equals(itemId))) {
            throw new NotFoundException("Only owner can change this");
        }

        ItemExtendedDto tempDto = findItemById(ownerId, itemId);
        Item tempItem = ItemMapper.toUpdatedItem(tempDto, changes);
        tempItem.setOwner(getUserById(ownerId));

        return ItemMapper.toItemDto(itemRepository.save(tempItem));
    }

    @Transactional
    @Override
    public CommentExtendedDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        User user = getUserById(userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        bookingRepository.findAllByBooker(user, sort).stream()
                .filter(booking -> booking.getItem().getId().equals(itemId)
                        && booking.getBooker().getId().equals(userId)
                        && booking.getEnd().isBefore(LocalDateTime.now())
                        && booking.getStatus().equals(BookingStatus.APPROVED))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        String.format("User:%d can not leave a comment for Item:%d.", userId, itemId)));

        Comment tempComment = CommentMapper.toComment(commentDto);
        tempComment.setUser(user);
        tempComment.setItem(itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item:%d does not exist.", itemId))));
        tempComment.setAuthorName(user.getName());

        return CommentMapper.toCommentDto(commentRepository.save(tempComment));
    }

    private BookingItemDto getLastBooking(List<BookingItemDto> bookingItemsDto) {
        return bookingItemsDto.stream()
                .filter(dto -> dto.getStart().isBefore(LocalDateTime.now()) &&
                        dto.getStatus().equals(BookingStatus.APPROVED))
                .reduce((first, second) -> second)
                .orElse(null);
    }

    private BookingItemDto getNextBooking(List<BookingItemDto> bookingItemsDto) {
        return bookingItemsDto.stream()
                .filter(dtoBooking -> (dtoBooking.getStart().isAfter(LocalDateTime.now()) ||
                        dtoBooking.getStart().isEqual(LocalDateTime.now())) &&
                        dtoBooking.getStatus().equals(BookingStatus.APPROVED))
                .findFirst()
                .orElse(null);
    }

    private User getUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User does not exist"));
    }

    private Item addRequestItem(Long requestId, Item requestedItem) {
        if (requestId == null) {
            return requestedItem;
        }
        Request tempRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("User does not exist"));
        requestedItem.setRequest(tempRequest);
        return requestedItem;
    }

    private void checkRequestExist(Long requestId) {
        requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Item request %d not found.", requestId)));
    }
}

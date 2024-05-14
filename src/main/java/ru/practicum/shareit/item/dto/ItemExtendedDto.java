package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

@Data
@Builder
public class ItemExtendedDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingItemDto nextBooking;
    private BookingItemDto lastBooking;
    private List<Comment> comments;
}

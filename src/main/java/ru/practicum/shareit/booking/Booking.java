package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@Data
public class Booking {
    private Long id;
    private Date start;
    private Date end;
    private ItemDto item;
    private User booker;
    private bookingStatus status;
}

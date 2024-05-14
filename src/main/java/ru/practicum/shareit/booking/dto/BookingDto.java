package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime start;
    @NotNull
    @Future
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime end;
    private Long itemId;
    private Long booker;
}

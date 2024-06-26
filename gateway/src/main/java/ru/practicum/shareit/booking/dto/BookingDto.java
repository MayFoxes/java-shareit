package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
    @NotNull
    @Positive
    private Long itemId;
    @Positive
    private Long booker;

    @AssertTrue(message = "Time validation error")
    private boolean isTimeValid() {
        return start != null && end != null && end.isAfter(start);
    }
}

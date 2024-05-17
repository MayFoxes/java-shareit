package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "BOOKINGS")
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "START_TIME", nullable = false)
    private LocalDateTime start;

    @Column(name = "END_TIME", nullable = false)
    private LocalDateTime end;

    @ManyToOne
    private Item item;
    @ManyToOne
    private User booker;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}

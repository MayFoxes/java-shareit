package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    ItemRepository itemRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private ItemService itemService;

    private ItemDto item;
    private UserDto user;
    private BookingDto createBooking;
    private Booking booking;

    @BeforeEach
    void setUp() {
        item = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .owner(1L)
                .available(true)
                .build();
        user = UserDto.builder().build();
        createBooking = BookingDto.builder()
                .start(LocalDateTime.of(2024, 10, 10, 10, 10, 0))
                .end(LocalDateTime.of(2024, 12, 10, 10, 10, 0))
                .itemId(item.getId())
                .booker(1L)
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 10, 10, 10, 0))
                .end(LocalDateTime.of(2024, 12, 10, 10, 10, 0))
                .item(ItemMapper.toItem(item))
                .booker(User.builder()
                        .id(1L)
                        .build())
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    @SneakyThrows
    void createBookingTest() {
        when(bookingService.createBooking(any(), any(Long.class)))
                .thenReturn(BookingMapper.toBookingExtendedDto(booking));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(createBooking))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @SneakyThrows
    void createNotFoundItemTest() {
        when(bookingService.createBooking(any(), any(Long.class)))
                .thenAnswer(invocationOnMock -> {
                    throw new NotFoundException("Error");
                });

        BookingDto booking = BookingDto.builder()
                .start(LocalDateTime.of(2024, 12, 12, 12, 12, 0))
                .end(LocalDateTime.of(2025, 1, 12, 12, 12, 0))
                .itemId(999L)
                .build();

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getBookingByIdTest() {
        when(bookingService.getBookingById(any(Long.class), any(Long.class)))
                .thenReturn(BookingMapper.toBookingExtendedDto(booking));

        mockMvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @SneakyThrows
    void getItemOwnerBookingsTest() {
        when(bookingService.getItemOwnerBookings(any(Long.class), any(BookingState.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(BookingMapper.toBookingExtendedDto(booking)));

        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .content(objectMapper.writeValueAsString(createBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(booking.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(booking.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @SneakyThrows
    void getItemOwnerBookingsByUnsupportedStateTest() {
        when(bookingService.getItemOwnerBookings(any(Long.class), any(BookingState.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(BookingMapper.toBookingExtendedDto(booking)));

        mockMvc.perform(get("/bookings/owner?state=UNSUPPORTED")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException));
    }

    @Test
    @SneakyThrows
    void getBookingsByUserTest() {
        when(bookingService.getUserBookings(any(Long.class), any(BookingState.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(BookingMapper.toBookingExtendedDto(booking)));

        mockMvc.perform(get("/bookings")
                        .content(objectMapper.writeValueAsString(createBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start",
                        is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end",
                        is(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @SneakyThrows
    void approveBookingTest() {
        when(bookingService.approveOrRejectBooking(1L, 1L, true))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @SneakyThrows
    void rejectBookingTest() {
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingService.approveOrRejectBooking(1L, 1L, false))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/1?approved=false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(booking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(booking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}
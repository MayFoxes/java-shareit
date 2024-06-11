package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestClient client;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @Valid @RequestBody RequestDto dto,
            @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Request to create item request.");
        return client.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Request to get item requests.");
        return client.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size,
            @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Request get all item requests.");
        if (from != null && size != null) {
            return client.getAllRequests(userId, from, size);
        }
        return client.getAll(userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequestById(
            @PathVariable Long requestId,
            @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Request to get item request.");
        return client.getById(userId, requestId);
    }
}

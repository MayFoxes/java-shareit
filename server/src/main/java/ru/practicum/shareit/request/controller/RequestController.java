package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExtendedDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@AllArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService requestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public Request createRequest(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @RequestBody RequestDto requestDto) {
        Request request = requestService.createRequest(ownerId, requestDto);
        log.info("A try to create a new item request:{}, by user with id:{}", requestDto, ownerId);
        return request;
    }

    @GetMapping("/{requestId}")
    public RequestExtendedDto findRequestsById(
            @RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long requestId) {
        log.info("User:{} get item request by id:{}.", userId, requestId);
        return requestService.findRequestById(userId, requestId);
    }

    @GetMapping
    public List<RequestExtendedDto> findAllRequestsByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("User:{} requested list of own requests.", ownerId);
        return requestService.findRequestsByOwnerId(ownerId);
    }

    @GetMapping("/all")
    public List<RequestExtendedDto> findAllRequests(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("User:{} requested list of requests.", userId);
        return requestService.findAllRequests(userId, from, size);
    }
}

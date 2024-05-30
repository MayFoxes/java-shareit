package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExtendedDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(Long userId, RequestDto requestDto);

    List<RequestExtendedDto> findRequestsByOwnerId(Long ownerId);

    List<RequestExtendedDto> findAllRequests(Long userId, Integer from, Integer size);

    RequestExtendedDto findRequestById(Long userId, Long id);
}

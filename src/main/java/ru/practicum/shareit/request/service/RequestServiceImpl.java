package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExtendedDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public Request createRequest(Long userId, RequestDto requestDto) {
        User tempUser = checkUserExist(userId);
        Request tempRequest = RequestMapper.toRequest(requestDto);
        tempRequest.setCreated(LocalDateTime.now());
        tempRequest.setUser(tempUser);

        return requestRepository.save(tempRequest);
    }

    @Override
    public List<RequestExtendedDto> findRequestsByOwnerId(Long ownerId) {
        checkUserExist(ownerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<Request> requests = requestRepository.findAllByUserId(ownerId, sort);

        return getListOfExtendedRequest(requests);
    }

    @Override
    public List<RequestExtendedDto> findAllRequests(Long userId, Integer from, Integer size) {
        checkUserExist(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from, size, sort);
        List<Request> requests = requestRepository.findAllByUserIdNot(userId, page);

        return getListOfExtendedRequest(requests);
    }

    @Override
    public RequestExtendedDto findRequestById(Long userId, Long requestId) {
        checkUserExist(userId);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Item request:%d does not exist.", requestId)));

        RequestExtendedDto tempRequest = RequestMapper.toExtendedRequest(request);
        tempRequest.setItems(itemRepository.findAllByRequest(request)
                .stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));

        return tempRequest;
    }

    private User checkUserExist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User:%d does not exist.", userId)));
    }

    private List<RequestExtendedDto> getListOfExtendedRequest(List<Request> requests) {
        List<RequestExtendedDto> listOfRequests = new ArrayList<>();

        for (Request request : requests) {
            RequestExtendedDto requestExtendedDto = RequestMapper.toExtendedRequest(request);
            requestExtendedDto.setItems(itemRepository.findAllByRequest(request)
                    .stream().map(ItemMapper::toItemDto)
                    .collect(Collectors.toList()));

            listOfRequests.add(requestExtendedDto);
        }
        return listOfRequests;
    }
}

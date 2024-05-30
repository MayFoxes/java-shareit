package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.Request;

@UtilityClass
public class RequestMapper {

    public Request toRequest(RequestDto requestDto) {
        return Request.builder()
                .description(requestDto.getDescription())
                .created(requestDto.getCreated())
                .build();
    }

    public RequestExtendedDto toExtendedRequest(Request request) {
        return RequestExtendedDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

}

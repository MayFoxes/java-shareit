package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class RequestDto {
    @NotNull
    @NotEmpty
    @JsonProperty("description")
    private String description;

    @JsonCreator
    public RequestDto(@JsonProperty("description") String description) {
        this.description = description;
    }
}

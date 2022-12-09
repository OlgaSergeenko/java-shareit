package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateRequestDto {

    @NotEmpty(message = "name required")
    private String name;
    @NotEmpty(message = "description required")
    private String description;
    @NotNull(message = "availability required")
    private Boolean available;
    private Long requestId;
}

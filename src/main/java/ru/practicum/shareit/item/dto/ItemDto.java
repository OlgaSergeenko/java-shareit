package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotEmpty (message = "description should not be empty")
    private String name;
    @NotNull (message = "description should not be null")
    private String description;
    @NotNull (message = "availability should not be null")
    private Boolean available;
    private UserDto owner;
    private Long requestId;
}

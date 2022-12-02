package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotNull (message = "name required")
    private String name;
    @Email (message = "email format - xxx@xxx.ru")
    @NotNull (message = "email required")
    private String email;
}

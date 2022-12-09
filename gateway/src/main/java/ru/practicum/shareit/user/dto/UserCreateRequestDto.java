package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestDto {

    @NotNull(message = "name required")
    private String name;
    @Email(message = "email format - xxx@xxx.ru")
    @NotNull(message = "email required")
    private String email;
}

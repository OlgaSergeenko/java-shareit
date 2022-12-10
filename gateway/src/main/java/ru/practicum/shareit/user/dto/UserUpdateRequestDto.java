package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {

    private String name;
    @Email(message = "email format - xxx@xxx.ru")
    private String email;
}

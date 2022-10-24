package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
    @NotNull
    @NotNull
    private String name;
    @NotNull
    @NotNull
    @Email
    private String email;
}

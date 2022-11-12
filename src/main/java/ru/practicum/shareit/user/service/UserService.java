package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long id);

    UserDto findById(Long userId);

    List<UserDto> getAll();

    void removeUser(long id);


}

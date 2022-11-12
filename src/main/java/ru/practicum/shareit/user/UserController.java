package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userService.create(user);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable("id") long userId,
                                          @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User userUpdated = userService.update(user, userId);
        return ResponseEntity.ok(UserMapper.toDto(userUpdated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") long userId) {
        User user = userService.getById(userId);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.getAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable("id") long userId) {
        userService.removeUser(userId);
    }
}

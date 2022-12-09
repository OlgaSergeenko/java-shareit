package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        UserDto userSaved = userService.create(userDto);
        log.info(String.format("User with id %d is created", userSaved.getId()));
        return ResponseEntity.ok(userSaved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable("id") Long userId,
                                          @RequestBody UserDto userDto) {
        userDto.setId(userId);
        UserDto userUpdated = userService.update(userDto);
        log.info(String.format("User with id %d is updated", userUpdated.getId()));
        return ResponseEntity.ok(userUpdated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long userId) {
        UserDto userFound = userService.getById(userId);
        log.info(String.format("User with id %d is found", userFound.getId()));
        return ResponseEntity.ok(userFound);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable Long userId) {
        log.info("Remove user {}", userId);
        userService.removeUser(userId);
    }
}

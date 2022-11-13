package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
        UserDto userSaved = userService.create(userDto);
        log.info(String.format("User with id %d is created", userSaved.getId()));
        return ResponseEntity.ok(userSaved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable("id") Long userId,
                                          @RequestBody UserDto userDto) {
        UserDto userUpdated = userService.update(userDto, userId);
        log.info(String.format("User with id %d is updated", userUpdated.getId()));
        return ResponseEntity.ok(userUpdated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long userId) {
        UserDto userFound = userService.findById(userId);
        log.info(String.format("User with id %d is found", userFound.getId()));
        return ResponseEntity.ok(userFound);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable("id") Long userId) {
        userService.removeUser(userId);
    }
}

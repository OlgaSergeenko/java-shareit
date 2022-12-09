package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get all users");
        return userClient.getUsers();
    }

    @PostMapping
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserCreateRequestDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.saveNewUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") @Positive Long userId,
                                         @RequestBody UserUpdateRequestDto userDto) {
        log.info("Updating user {}, id = {}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") @Positive Long userId) {
        log.info("Get user {}", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable("id") Long userId) {
        log.info("Remove user {}", userId);
        userClient.removeUser(userId);
    }
}

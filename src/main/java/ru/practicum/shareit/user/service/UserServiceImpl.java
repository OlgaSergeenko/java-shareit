package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toDto(userRepository.save(user));
    }

    public UserDto update(UserDto userDto, Long id) {
        User user = UserMapper.toUser(userDto);
        Optional<User> userToUpdate = userRepository.findById(id);
        if (userToUpdate.isEmpty()) {
            log.error(String.format("No user with id %d is found to update", id));
            throw new UserNotFoundException("User is not found");
        }

        Optional.ofNullable(user.getName()).ifPresent(userToUpdate.get()::setName);
        Optional.ofNullable(user.getEmail()).ifPresent(userToUpdate.get()::setEmail);
        return UserMapper.toDto(userRepository.save(userToUpdate.get()));
    }

    public UserDto findById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.error(String.format("No user with id %d is found", userId));
            throw new UserNotFoundException("User is not found");
        }
        return UserMapper.toDto(user.get());
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public void removeUser(long id) {
        userRepository.deleteById(id);
    }

    public UserDto findUserIfExistOrElseThrowNotFound(Long userId) {
        return findById(userId);
    }
}

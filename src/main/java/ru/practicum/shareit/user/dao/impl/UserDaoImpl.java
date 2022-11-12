package ru.practicum.shareit.user.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@Slf4j
public class UserDaoImpl implements UserDao {

    private List<User> users = new ArrayList<>();

    private long id = 0;

    @Override
    public User create(User user) {
        user.setId(generateId());
        users.add(user);
        return user;
    }

    @Override
    public User update(User user, long id) {
        User oldUser = getById(id);

        Optional.ofNullable(user.getEmail()).ifPresent(oldUser::setEmail);
        Optional.ofNullable(user.getName()).ifPresent(oldUser::setName);

        return oldUser;
    }

    @Override
    public User getById(long userId) {
        try {
            return users.stream()
                    .filter(user -> user.getId() == userId)
                    .findFirst().orElseThrow();
        } catch (NoSuchElementException e) {
            log.error("No users with id {} is found", userId);
            throw new UserNotFoundException("User is not found");
        }
    }

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public void removeUser(long id) {
        try {
            users.remove(users.stream().filter(user -> user.getId() == id).findFirst().orElseThrow());
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException(String.format("User %d does not exist", id));
        }
    }

    private long generateId() {
        return ++id;
    }
}

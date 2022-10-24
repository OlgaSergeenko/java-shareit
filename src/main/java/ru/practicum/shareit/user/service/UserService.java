package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserDao userDao;

    public User create(User user) {
        validateEmail(user.getEmail());
        return userDao.create(user);
    }

    public User update(User user, long id) {
        validateEmail(user.getEmail());
        return userDao.update(user, id);
    }

    public User getById(long userId) {
        return userDao.getById(userId);
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public void removeUser(long id) {
        userDao.removeUser(id);
    }

    private void validateEmail(String email) {
        if (userDao.getAll().stream().map(User::getEmail).anyMatch(k -> k.equals(email))) {
            throw new DuplicateEmailException(String.format("User with email %s is already exist", email));
        }
    }
}

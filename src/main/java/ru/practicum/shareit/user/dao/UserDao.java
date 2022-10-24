package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {

    User create(User user);

    User update(User user, long id);

    User getById(long userId);

    List<User> getAll();

    void removeUser(long id);
}

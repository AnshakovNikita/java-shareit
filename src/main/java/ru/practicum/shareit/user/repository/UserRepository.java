package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    Collection<User> findAll();

    User getById(long userId);

    User add(User user);

    void delete(long userId);
}
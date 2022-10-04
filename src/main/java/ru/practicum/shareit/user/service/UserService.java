package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto addUser(User user);

    UserDto updateUser(long userId, User updatedUser);

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers();

    void deleteUser(long userId);
}
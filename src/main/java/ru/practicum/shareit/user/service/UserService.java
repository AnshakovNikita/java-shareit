package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(long userId, User updatedUser);

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers();

    Set<Item> getUserItems(long userId);

    void deleteUser(long userId);
}
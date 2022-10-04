package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityAlreadyExistException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(User user) {
        checkIsEmailIsExist(user);
        return UserMapper.toUserDto(userRepository.add(user));
    }

    @Override
    public UserDto updateUser(long userId, User updatedUser) {
        if (userRepository.getById(userId) == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        User user = userRepository.getById(userId);
        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            checkIsEmailIsExist(updatedUser);
            user.setEmail(updatedUser.getEmail());
        }
        return UserMapper.toUserDto(userRepository.add(user));
    }

    @Override
    public UserDto getUserById(long userId) {
        if (userRepository.getById(userId) == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return UserMapper.toUserDto(userRepository.getById(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(long userId) {
        if (userRepository.getById(userId) == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        userRepository.delete(userId);
    }

    private void checkIsEmailIsExist(User user) {
        List<User> users = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .collect(Collectors.toList());
        if (!users.isEmpty()) {
            throw new EntityAlreadyExistException("Указанный email уже существует");
        }
    }
}
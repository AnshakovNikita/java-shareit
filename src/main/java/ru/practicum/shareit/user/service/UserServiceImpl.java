package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto updateUser(long userId, User updatedUser) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new EntityNotFoundException("Пользователь не найден");
        });

        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new EntityNotFoundException("Пользователь не найден");
        });
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new EntityNotFoundException("Пользователь не найден");
        });
        userRepository.deleteById(userId);
    }

    public Set<Item> getUserItems(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new EntityNotFoundException("Пользователь не найден");
        });
        return user.getUserItems();
    }


}
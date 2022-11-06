package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping()
    public UserDto postUser(@RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя = {}", userDto);
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@RequestBody UserDto user,
                             @PathVariable long userId) {
        log.info("Получен запрос на обновление данных {}= пользователя с id = {}", user, userId);
        return userService.updateUser(user, userId);
    }

    @GetMapping()
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Получен запрос на получение пользователя с id = {}", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);
        userService.deleteUser(userId);
    }
}
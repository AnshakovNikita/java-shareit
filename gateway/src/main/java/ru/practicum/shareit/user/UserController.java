package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping()
    public ResponseEntity<Object> postUserGateway(@Valid @RequestBody UserDto userDto) {
        log.info("create user, userDto {}", userDto);
        return userClient.postUser(userDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object>  getUserByIdGateWay(@PathVariable Long id) {
        log.info("UserController.getUserGateWay Get user id = {} ", id);
        return userClient.getUserById(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> patchUserGateWay(@RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("UserController.updateUserGateWay userDto Email = {}, Name = {}", userDto.getEmail(), userDto.getName());
        return userClient.patchUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> deleteUserByIdGateway(@PathVariable Long id) {
        log.info("UserController.deleteUserGateway user id = {} ", id);
        return userClient.deleteUserById(id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUserGateWay() {
        log.info("UserController.getUsersGateWay");
        return userClient.getAllUser();
    }

}
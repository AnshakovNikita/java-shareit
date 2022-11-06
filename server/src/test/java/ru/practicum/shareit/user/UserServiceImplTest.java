package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    private final UserService userService = new UserServiceImpl(userRepository);

    private User user;
    private UserDto userDto;
    private List<User> list;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user1", "user1@mail.com");
        userDto = UserDto.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.com")
                .build();

        list = Collections.singletonList(user);
    }

    @Test
    void getUsersTest() {
        Mockito
                .when(userRepository.findAll()).thenReturn(list);
        List<UserDto> listTest = userService.getAllUsers();
        Assertions.assertEquals(1L, listTest.get(0).getId());
        Assertions.assertEquals("user1", listTest.get(0).getName());
        Assertions.assertEquals("user1@mail.com", listTest.get(0).getEmail());
    }

    @Test
    void getUserTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDto userDtoTest = userService.getUserById(1L);
        Assertions.assertEquals(1L, userDtoTest.getId());
        Assertions.assertEquals("user1", userDtoTest.getName());
        Assertions.assertEquals("user1@mail.com", userDtoTest.getEmail());
    }

    @Test
    void deleteWrongUserTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(2L));
    }

    @Test
    void addUserTest() {
        Mockito
                .when(userRepository.save(user)).thenReturn(user);
        UserDto userDtoTest = userService.addUser(userDto);
        Assertions.assertEquals(1L, userDtoTest.getId());
        Assertions.assertEquals("user1", userDtoTest.getName());
        Assertions.assertEquals("user1@mail.com", userDtoTest.getEmail());
    }

    @Test
    void updateUserTest() {
        Mockito
                .when(userRepository.existsById(1L)).thenReturn(true);
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.save(user)).thenReturn(user);
        Mockito
                .when(userService.updateUser(userDto, 1L)).thenReturn(userDto);

        Assertions.assertEquals(1L, userDto.getId());
        Assertions.assertEquals("user1", userDto.getName());
        Assertions.assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void updateUserExceptionTest() {
        Mockito
                .when(userRepository.save(user)).thenReturn(user);
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.updateUser(userDto, 500L));
    }

    @Test
    void getUserByIdNotFoundTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUserById(100L));
    }

    @Test
    void getUserItemsIdNotFoundTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUserItems(100L));
    }
}
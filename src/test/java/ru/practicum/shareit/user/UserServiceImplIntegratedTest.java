package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegratedTest {

    private final EntityManager em;
    private final UserService userService;

    private final UserRepository userRepository;

    private User create;

    @BeforeEach
    void beforeEach() {
        create = User.builder()
                .name("John")
                .email("some@email.com")
                .build();
        em.persist(create);
    }

    @Test
    void addUser() {
        final UserDto userDto = new UserDto(1L, "userDto", "user2@email.com");

        em.persist(create);

        UserDto resUserDto = userService.addUser(userDto);
        User resUser = userRepository.findById(resUserDto.getId()).get();

        assertThat(resUser.getId(), equalTo(resUserDto.getId()));
        assertThat(resUser.getName(), equalTo(resUserDto.getName()));
        assertThat(resUser.getEmail(), equalTo(resUserDto.getEmail()));
    }

    @Test
    void updateUserEmailAndName() {
        final Long id = create.getId();
        final User upUser = User.builder()
                .name("newName")
                .email("new@email.com")
                .build();

        userService.updateUser(id, upUser);

        User resUser = userRepository.findById(id).get();

        assertThat(resUser.getId(), equalTo(id));
        assertThat(resUser.getName(), equalTo("newName"));
        assertThat(resUser.getEmail(), equalTo("new@email.com"));
    }

    @Test
    void updateUserName() {
        final Long id = create.getId();
        final User upUser = User.builder()
                .name("newName")
                .build();

        userService.updateUser(id, upUser);

        User resUser = userRepository.findById(id).get();

        assertThat(resUser.getId(), equalTo(id));
        assertThat(resUser.getName(), equalTo("newName"));
        assertThat(resUser.getEmail(), equalTo("some@email.com"));
    }

    @Test
    void updateUserEmail() {
        final Long id = create.getId();
        final User upUser = User.builder()
                .email("new@email.com")
                .build();

        userService.updateUser(id, upUser);

        User resUser = userRepository.findById(id).get();

        assertThat(resUser.getId(), equalTo(id));
        assertThat(resUser.getName(), equalTo("John"));
        assertThat(resUser.getEmail(), equalTo("new@email.com"));
    }

    @Test
    void updateUserNotFoundException() {
        final Long id = create.getId();
        userService.deleteUser(id);
        final User user = User.builder()
                .name("John")
                .email("some@email.com")
                .build();

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateUser(id, user)
        );

        assertThat("Пользователь не найден", equalTo(exception.getMessage()));
    }
}
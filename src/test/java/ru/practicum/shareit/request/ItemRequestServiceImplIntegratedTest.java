package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplIntegratedTest {

    private final ItemRequestService itemRequestService;

    private final UserRepository userRepository;

    private final ItemRequestRepository itemRequestRepository;

    private ItemRequest itemRequest;

    private User requester;

    @BeforeEach
    void beforeEach() {
        requester = User.builder()
                .name("user")
                .email("user@email.com")
                .build();
        userRepository.save(requester);
        itemRequest = ItemRequest.builder()
                .description("description")
                .requesterId(requester.getId())
                .created(LocalDateTime.now())
                .build();
        itemRequestRepository.save(itemRequest);
    }


    @Test
    void createTest() {
        final Long userId = requester.getId();
        final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("some text")
                .build();
        itemRequestRepository.deleteById(itemRequest.getId());

        final var resultDto = itemRequestService.createItemRequest(userId, itemRequestDto);
        final var result = itemRequestRepository.findById(resultDto.getId()).get();

        assertThat(resultDto.getId(), equalTo(result.getId()));
        assertThat(resultDto.getDescription(), equalTo(result.getDescription()));
        assertThat(resultDto.getRequesterId(), equalTo(result.getRequesterId()));
        assertThat(resultDto.getItems(), equalTo(null));
    }

    @Test
    void updateUserNotFoundExceptionTest() {
        final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("some text pls")
                .build();
        itemRequestRepository.deleteById(itemRequest.getId());

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemRequestService.createItemRequest(9999L, itemRequestDto)
        );

        assertThat("Пользователь не найден", equalTo(exception.getMessage()));
    }

    @Test
    void findAllByRequesterTest() {
        final Long requesterId = requester.getId();
        List<ItemRequestDto> resList = List.of(ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getRequesterId())
                .created(itemRequest.getCreated())
                .items(List.of())
                .build()
        );

        final var result = itemRequestService.findAllByRequester(requesterId);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(resList.get(0).getId()));
        assertThat(result.get(0).getDescription(), equalTo(resList.get(0).getDescription()));
        assertThat(result.get(0).getRequesterId(), equalTo(resList.get(0).getRequesterId()));
        assertThat(result.get(0).getCreated(), equalTo(resList.get(0).getCreated()));
        assertThat(result.get(0).getItems(), equalTo(resList.get(0).getItems()));
    }

    @Test
    void findAllByRequesterUserNotFoundExceptionTest() {
        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemRequestService.findAllByRequester(9999L)
        );

        assertThat("Пользователь не найден", equalTo(exception.getMessage()));
    }
}
package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotAvailableException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private ItemRequestService itemRequestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private final LocalDateTime time = LocalDateTime.now();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("description")
            .requesterId(1L)
            .created(time)
            .items(List.of())
            .build();


    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRepository, itemRequestRepository);
    }

    @Test
    void createUserNotFoundExceptionExistsTest() {
        final Long userId = 1L;
        final ItemRequestDto createDto = ItemRequestDto.builder()
                .description("description")
                .requesterId(1L)
                .created(time)
                .build();

        when(userRepository.existsById(userId))
                .thenThrow(new EntityNotFoundException("Пользователь не найден"));

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemRequestService.createItemRequest(userId, createDto)
        );

        assertThat("Пользователь не найден", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
    }

    @Test
    void findAllByRequester() {
    }

    @Test
    void findAllUserNotFoundExceptionExistsTest() {
        final Long userId = 1L;
        final Integer from = 0;
        final Integer size = 10;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        final var exception = assertThrows(
                EntityNotAvailableException.class,
                () -> itemRequestService.findAll(userId, from, size)
        );

        assertThat("Пользователь не найден", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
    }

    @Test
    void findById() {
        final Long userId = 1L;
        final Long requestId = 1L;
        final ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requesterId(1L)
                .created(time)
                .build();

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRequestRepository.existsById(requestId))
                .thenReturn(true);
        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(requestId))
                .thenReturn(List.of());

        final var result = itemRequestService.findById(userId, requestId);

        assertThat(result.getId(), equalTo(itemRequestDto.getId()));
        assertThat(result.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(result.getRequesterId(), equalTo(itemRequestDto.getRequesterId()));
        assertThat(result.getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(result.getItems(), equalTo(itemRequestDto.getItems()));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(itemRequestRepository, times(1))
                .existsById(requestId);
        verify(itemRequestRepository, times(1))
                .findById(requestId);
        verify(itemRepository, times(1))
                .findAllByRequestId(requestId);

    }

    @Test
    void findByIdItemRequestNotFoundExceptionExistsTest() {
        final Long userId = 1L;
        final Long requestId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRequestRepository.existsById(requestId))
                .thenReturn(false);

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemRequestService.findById(userId, requestId)
        );

        assertThat("Запрос не найден", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(itemRequestRepository, times(1))
                .existsById(requestId);
    }

    @Test
    void findByIdUserNotFoundExceptionExistsTest() {
        final Long userId = 1L;
        final Long requestId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemRequestService.findById(userId, requestId)
        );

        assertThat("Пользователь не найден", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
    }

    @Test
    void findByIdItemRequestNotFoundExceptionFindByIdTest() {
        final Long userId = 1L;
        final Long requestId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRequestRepository.existsById(requestId))
                .thenReturn(true);
        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemRequestService.findById(userId, requestId)
        );

        assertThat("Запрос не существует", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(itemRequestRepository, times(1))
                .existsById(requestId);
        verify(itemRequestRepository, times(1))
                .findById(requestId);
    }

    @Test
    void itemFullPrintDtoTest() {
        User owner = User.builder()
                .name("owner")
                .email("owner@email.com")
                .build();
        Item item = new Item(1L, "item", "description", true, null, owner.getId());

        final var result = ItemMapper.toItemDto(item);

        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.getLastBooking(), equalTo(null));
        assertThat(result.getNextBooking(), equalTo(null));
        assertThat(result.getComments().size(), equalTo(0));
    }
}
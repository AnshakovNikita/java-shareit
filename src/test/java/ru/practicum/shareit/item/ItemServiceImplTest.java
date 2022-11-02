package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private final ItemRequestRepository itemRequestRepository = Mockito.mock(ItemRequestRepository.class);

    private final UserService userService = Mockito.mock(UserServiceImpl.class);

    private final ItemService itemService = new ItemServiceImpl(itemRepository, userService,
            commentRepository, bookingRepository);

    private Item item;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private Comment comment;
    private User user, userNew;
    private UserDto userDto;
    private BookingDto bookingDto;
    private List<Booking> list;
    private List<ItemDto> listItemDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime localDateTime = LocalDateTime.now();
        user = new User(1L, "user1", "user1@mail.com");
        userNew = new User(2L,"user2","user2@mail.com");
        userDto = new UserDto(1L,"user1","user1@mail.com");
        itemRequest = new ItemRequest(1L,"description", user.getId(), localDateTime.minusMonths(2));
        item = new Item(1L,"item","desc",true, itemRequest.getId(), userNew.getId());
        itemDto = new ItemDto(1L,"item","desc",true, itemRequest.getId());
        comment = new Comment(1L, "text", item, user,  LocalDateTime.now());
        commentDto = new CommentDto(1L,"text", itemDto.getId(), "user1", LocalDateTime.now());


        Booking booking = new Booking(1L, BookingStatus.REJECTED, userNew, item, LocalDateTime.now(), localDateTime.plusMonths(2));

        bookingDto = new BookingDto(1L, BookingStatus.WAITING, userNew.getId(), item.getId(), localDateTime,
                localDateTime.plusMonths(2), item.getName());

        list = Collections.singletonList(booking);
        listItemDto = Collections.singletonList(itemDto);
    }

    @Test
    void addItemWithoutNameTest() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemRepository.save(item))
                .thenReturn(item);
        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.addItem(
                new ItemDto(1L,"item",null,true, 1L), 1L));
    }

    @Test
    void addItemWithoutNDescTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemRepository.save(item)).thenReturn(item);
        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.addItem(
                new ItemDto(1L,"item",null,true, 1L), 1L));
    }

    @Test
    void addItemWithoutAvailableTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemRepository.save(item)).thenReturn(item);
        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.addItem(
                new ItemDto(1L,"item","null",true, 1L), 1L));
    }

    @Test
    void getItemTest() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.findById(2L))
                .thenReturn(Optional.of(userNew));
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        ItemDto itemTest = itemService.getById(1L, 1L);
        Assertions.assertEquals(1L, itemTest.getId());
        Assertions.assertEquals("item", itemTest.getName());
        Assertions.assertEquals("desc", itemTest.getDescription());

        ItemDto itemTest2 = itemService.getById(1L, 2L);
        Assertions.assertEquals(1L, itemTest2.getId());
        Assertions.assertEquals("item", itemTest2.getName());
        Assertions.assertEquals("desc", itemTest2.getDescription());
    }

    @Test
    void getItemWrongNullTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.getById(null, 1L));
    }

    @Test
    void getItemWrongIdTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.getById(100L, 1L));
    }

    @Test
    void updateItemWrongOwnerTest() {
        Mockito
                .when(userRepository.findById(2L)).thenReturn(Optional.of(userNew));
        Mockito
                .when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Mockito
                .when(itemRepository.save(item)).thenReturn(item);
        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemDto, 1L, 1L));
    }

    @Test
    void addCommentWithoutTextTest() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.findBookingsByBookerAndItemAndStatusNot(1L, 1L, BookingStatus.APPROVED))
                .thenReturn(list);
        Mockito
                .when(commentRepository.save(comment))
                .thenReturn(comment);
        Assertions.assertThrows(IllegalStateException.class, () -> itemService.addComment(1L,
                1L, new CommentDto(1L,"", itemDto.getId(),"user1", LocalDateTime.now())));
    }

    @Test
    void addCommentWrongBookingTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.findBookingsByBookerAndItemAndStatusNot(1L, 1L, BookingStatus.APPROVED))
                .thenReturn(list);
        Mockito
                .when(commentRepository.save(comment)).thenReturn(comment);
        Assertions.assertThrows(IllegalStateException.class, () -> itemService.addComment(1L, 1L, commentDto));
    }

    @Test
    void getItemByIdFailTest() {
        Mockito
                .when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(100L));
    }

    @Test
    void getItemsWrongIdTest() {
        Mockito
                .when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.getItems(100L));
    }
}
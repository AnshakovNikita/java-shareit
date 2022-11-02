package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

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
public class ItemServiceImplIntegratedTest {

    private final ItemService itemService;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final UserService userService;

    private User createOwner;

    private Item createItem;

    @BeforeEach
    void beforeEach() {
        createOwner = User.builder()
                .name("John")
                .email("some@email.com")
                .build();
        userRepository.save(createOwner);

        createItem = Item.builder()
                .name("item1")
                .description("description")
                .available(true)
                .userId(createOwner.getId())
                .build();

        itemRepository.save(createItem);
    }

    @Test
    void addItem() {
        final ItemDto itemDto = new ItemDto(null, "itemDto", "descriptionDto", true, null);

        final ItemDto resItemDto = itemService.addItem(itemDto, createOwner.getId());
        final Item resItem = itemRepository.findById(resItemDto.getId()).get();

        assertThat(resItem.getId(), equalTo(resItemDto.getId()));
        assertThat(resItem.getName(), equalTo(resItemDto.getName()));
        assertThat(resItem.getDescription(), equalTo(resItemDto.getDescription()));
        assertThat(resItem.getAvailable(), equalTo(resItemDto.getAvailable()));
        assertThat(resItem.getRequestId(), equalTo(createItem.getRequestId()));
        assertThat(resItem.getUserId(), equalTo(createOwner.getId()));
    }

    @Test
    void addItemUserNotFoundException() {
        final Long userId = createOwner.getId();
        final ItemDto itemDto = new ItemDto(null, "itemDto", "descriptionDto", true, null);

        userRepository.deleteById(userId);

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.addItem(itemDto, userId)
        );

        assertThat("Пользователь не найден", equalTo(exception.getMessage()));
    }

    @Test
    void addComment() {
        final User author = User.builder()
                .name("John")
                .email("hfkg@email.com")
                .build();
        userRepository.save(author);
        final Booking booking = new Booking(null, BookingStatus.APPROVED, author, createItem,
                                                LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));

        bookingRepository.save(booking);
        final CommentDto commentdto = new CommentDto(null, "text", createItem.getId(), author.getName(), null);


        final var resCommentDto = itemService.addComment(author.getId(), createItem.getId(), commentdto);
        final var resComment = commentRepository.findById(resCommentDto.getId()).get();

        assertThat(resCommentDto.getId(), equalTo(resComment.getId()));
        assertThat(resCommentDto.getText(), equalTo(resComment.getText()));
        assertThat(resCommentDto.getAuthorName(), equalTo(resComment.getAuthor().getName()));
    }

    @Test
    void addCommentItemNotFoundBookingExceptionTest() {
        final Long userId = createOwner.getId();
        final Long itemId = createItem.getId();
        itemRepository.deleteById(itemId);
        final User author = User.builder()
                .name("John")
                .email("hfkg@email.com")
                .build();
        userRepository.save(author);
        final Booking booking = new Booking(null, BookingStatus.APPROVED, author, createItem,
                                                LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));

        bookingRepository.save(booking);
        final CommentDto commentdto = new CommentDto(null, "text", createItem.getId(), author.getName(), null);

        final var exception = assertThrows(
                IllegalStateException.class,
                () -> itemService.addComment(userId, itemId, commentdto)
        );

        assertThat("У предмета не было бронирований", equalTo(exception.getMessage()));
    }

    @Test
    void addCommentAccessBookingExceptionTest() {
        final Long userId = createOwner.getId();
        final Long itemId = createItem.getId();
        final User author = User.builder()
                .name("John")
                .email("hfkg@email.com")
                .build();
        userRepository.save(author);
        final Booking booking = new Booking(null, BookingStatus.APPROVED, author, createItem,
                                                LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));

        bookingRepository.save(booking);
        final CommentDto commentdto = new CommentDto(null, "text", createItem.getId(), author.getName(), null);

        final var exception = assertThrows(
                IllegalStateException.class,
                () -> itemService.addComment(userId, itemId, commentdto)
        );

        assertThat("У предмета не было бронирований", equalTo(exception.getMessage()));
    }

    @Test
    void getAllItemsUserTest() {
        final int from = 0;
        final int size = 10;
        final Long userId = createOwner.getId();
        final Long itemId = createItem.getId();

        final List<ItemDto> result = itemService.getAllItemsUser(userId, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(itemId));
        assertThat(result.get(0).getName(), equalTo(createItem.getName()));
        assertThat(result.get(0).getDescription(), equalTo(createItem.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(createItem.getAvailable()));
        assertThat(result.get(0).getLastBooking(), equalTo(null));
        assertThat(result.get(0).getNextBooking(), equalTo(null));
        assertThat(result.get(0).getComments(), equalTo(List.of()));
    }

    @Test
    void updateUserNotFoundExceptionTest() {
        final Long ownerId = createOwner.getId();
        final Long itemId = createItem.getId();
        userRepository.deleteById(ownerId);
        ItemDto itemDto = new ItemDto(null, "newName", "newDesc", true, null);

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.updateItem(itemDto, itemId, ownerId)
        );

        assertThat("Пользователь не найден", equalTo(exception.getMessage()));
    }

    @Test
    void updateItemNotFoundExceptionTest() {
        final Long ownerId = createOwner.getId();
        final Long itemId = 100L;
        ItemDto itemDto = new ItemDto(null, "newName", "newDesc", true, null);

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.updateItem(itemDto, itemId, ownerId)
        );
        assertThat("Товар не найден", equalTo(exception.getMessage()));
    }

    @Test
    void updateOwnerAccessException() {
        final User tempUser = User.builder()
                .name("Terry")
                .email("hfgffhg@email.com")
                .build();
        userRepository.save(tempUser);
        final Long itemId = createItem.getId();
        ItemDto itemDto = new ItemDto(null, "newName", "newDesc", true, null);

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.updateItem(itemDto, itemId, tempUser.getId())
        );

        assertThat("Предмет отсутсвует у данного пользователя", equalTo(exception.getMessage()));
    }

    @Test
    void addCommentFutureExceptionTest() {
        final User author = User.builder()
                .name("John")
                .email("hfkg@email.com")
                .build();
        userRepository.save(author);
        final Booking booking = new Booking(null, BookingStatus.APPROVED, author, createItem,
                LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(1));

        bookingRepository.save(booking);
        final CommentDto commentdto = new CommentDto(null, "text", createItem.getId(), author.getName(), null);

        final var exception = assertThrows(
                IllegalStateException.class,
                () -> itemService.addComment(author.getId(), createItem.getId(), commentdto)
        );

        assertThat("Комментарий не может быть оставлен к будущему бронированию", equalTo(exception.getMessage()));
    }

    @Test
    void getAllItemsTest() {
        final Long itemId = createItem.getId();

        final List<ItemDto> result = itemService.getItems(0);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(itemId));
        assertThat(result.get(0).getName(), equalTo(createItem.getName()));
        assertThat(result.get(0).getDescription(), equalTo(createItem.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(createItem.getAvailable()));
        assertThat(result.get(0).getLastBooking(), equalTo(null));
        assertThat(result.get(0).getNextBooking(), equalTo(null));
        assertThat(result.get(0).getComments(), equalTo(List.of()));
    }

    @Test
    void updateTest() {
        final Long ownerId = createOwner.getId();
        final Long itemId = createItem.getId();
        ItemDto itemDto = new ItemDto(null, "newName", "newDescription", true, null);

        ItemDto resItemDto = itemService.updateItem(itemDto, itemId, ownerId);

        assertThat(resItemDto.getId(), equalTo(createItem.getId()));
        assertThat(resItemDto.getDescription(), equalTo(createItem.getDescription()));
        assertThat(resItemDto.getName(), equalTo(createItem.getName()));
        assertThat(resItemDto.getAvailable(), equalTo(createItem.getAvailable()));
        assertThat(resItemDto.getRequestId(), equalTo(null));
    }

    @Test
    void searchItemTest() {
        final int from = 0;
        final int size = 10;
        final Long itemId = createItem.getId();

        final List<ItemDto> result = itemService.searchItem("item1", from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(itemId));
        assertThat(result.get(0).getName(), equalTo(createItem.getName()));
        assertThat(result.get(0).getDescription(), equalTo(createItem.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(createItem.getAvailable()));
        assertThat(result.get(0).getLastBooking(), equalTo(null));
        assertThat(result.get(0).getNextBooking(), equalTo(null));
        assertThat(result.get(0).getComments(), equalTo(List.of()));
    }

    @Test
    void searchItemUncTextTest() {
        final int from = 0;
        final int size = 10;

        final List<ItemDto> result = itemService.searchItem("", from, size);

        assertThat(result.size(), equalTo(0));
    }
}
package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturnDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotAvailableException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIntegratedTest {

    private final BookingRepository bookingRepository;

    private final BookingService bookingService;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private Booking booking;

    private User user;

    private User owner;

    private Item item;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .name("updateName")
                .email("updateName@user.com")
                .build();
        userRepository.save(user);
        owner = User.builder()
                .name("someName")
                .email("some@user.com")
                .build();
        userRepository.save(owner);
        item = Item.builder()
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .userId(owner.getId())
                .build();
        itemRepository.save(item);
    }

    @Test
    void createTest() {
        final LocalDateTime start = LocalDateTime.now().plusDays(1);
        final LocalDateTime end = LocalDateTime.now().plusDays(3);
        final BookingDto bookingCreateDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .build();
        final BookingReturnDto bookingPrintDto = BookingReturnDto.builder()
                .start(start)
                .end(end)
                .item(ItemMapper.toItemDto(item))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.WAITING)
                .build();

        final var result = bookingService.addBooking(bookingCreateDto, user.getId());

        assertNotNull(result.getId());
        assertThat(result.getBooker().getId(), equalTo(bookingPrintDto.getBooker().getId()));
        assertThat(result.getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void createUserNotFoundExceptionTest() {
        final LocalDateTime start = LocalDateTime.now().plusDays(1);
        final LocalDateTime end = LocalDateTime.now().plusDays(3);
        final BookingDto bookingCreateDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .build();

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.addBooking(bookingCreateDto, 100L)
        );

        assertThat("Пользователь не найден", equalTo(exception.getMessage()));
    }

    @Test
    void createDateTimeExceptionTest() {
        final LocalDateTime start = LocalDateTime.now().plusDays(3);
        final LocalDateTime end = LocalDateTime.now().plusDays(1);
        final BookingDto bookingCreateDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .build();

        final var exception = assertThrows(
                IllegalStateException.class,
                () -> bookingService.addBooking(bookingCreateDto, user.getId())
        );

        assertThat("Дата начала бронирования не может быть позже даты завершения", equalTo(exception.getMessage()));
    }

    @Test
    void createBookingAccessExceptionTest() {
        final LocalDateTime start = LocalDateTime.now().plusDays(1);
        final LocalDateTime end = LocalDateTime.now().plusDays(3);
        final BookingDto bookingCreateDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .build();

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.addBooking(bookingCreateDto, owner.getId())
        );

        assertThat("Невозможно забронировать свой предмет", equalTo(exception.getMessage()));
    }

    @Test
    void createAccessErrorForItemExceptionTest() {
        final LocalDateTime start = LocalDateTime.now().plusDays(1);
        final LocalDateTime end = LocalDateTime.now().plusDays(3);
        Item newItem = Item.builder()
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель")
                .available(false)
                .userId(owner.getId())
                .build();
        itemRepository.save(newItem);
        final BookingDto bookingCreateDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(newItem.getId())
                .build();

        final var exception = assertThrows(
                EntityNotAvailableException.class,
                () -> bookingService.addBooking(bookingCreateDto, user.getId())
        );

        assertThat("Предмет недоступен", equalTo(exception.getMessage()));

    }

    @Test
    void findAllByStateAllTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "ALL";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);
        final BookingReturnDto bookingPrintDto = BookingReturnDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(ItemMapper.toItemDto(item))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();

        final var result = bookingService.getUserBookingList(userId, from, size, state);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker().getId(), equalTo(bookingPrintDto.getBooker().getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStateRejectedTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "REJECTED";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .build();
        bookingRepository.save(booking);
        final BookingReturnDto bookingPrintDto = BookingReturnDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(ItemMapper.toItemDto(item))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.REJECTED)
                .build();

        final var result = bookingService.getUserBookingList(userId, from, size, state);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker().getId(), equalTo(bookingPrintDto.getBooker().getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStateFutureTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "FUTURE";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);
        final BookingReturnDto bookingPrintDto = BookingReturnDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(ItemMapper.toItemDto(item))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();

        final var result = bookingService.getUserBookingList(userId, from, size, state);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker().getId(), equalTo(bookingPrintDto.getBooker().getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }


    @Test
    void findAllByStateWaitingTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "WAITING";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);
        final BookingReturnDto bookingPrintDto = BookingReturnDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(ItemMapper.toItemDto(item))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.WAITING)
                .build();

        final var result = bookingService.getUserBookingList(userId, from, size, state);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker().getId(), equalTo(bookingPrintDto.getBooker().getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStateCurrentTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "CURRENT";
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);
        final BookingReturnDto bookingPrintDto = BookingReturnDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(ItemMapper.toItemDto(item))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();

        final var result = bookingService.getUserBookingList(userId, from, size, state);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker().getId(), equalTo(bookingPrintDto.getBooker().getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStatePastTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "PAST";
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);
        final BookingReturnDto bookingPrintDto = BookingReturnDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(ItemMapper.toItemDto(item))
                .booker(UserMapper.toUserDto(user))
                .status(BookingStatus.APPROVED)
                .build();

        final var result = bookingService.getUserBookingList(userId, from, size, state);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker().getId(), equalTo(bookingPrintDto.getBooker().getId()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStateUserNotFoundExceptionTest() {
        final int from = 0;
        final int size = 10;
        final String state = "ALL";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getUserBookingList(100L, from, size, state));

        assertThat("Пользователь не найден", equalTo(exception.getMessage()));

    }

    @Test
    void findAllByStateUnsupportedStateExceptionTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "INCORRECT";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);

        final var exception = assertThrows(
                EntityNotAvailableException.class,
                () -> bookingService.getUserBookingList(userId, from, size, state));

        assertThat("Unknown state: UNSUPPORTED_STATUS", equalTo(exception.getMessage()));
    }

    @Test
    void findAllByStateForOwnerUserNotFoundExceptionTest() {
        final int from = 0;
        final int size = 10;
        final String state = "ALL";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getOwnerBookingList(9999L, from, size, state));

        assertThat("Пользователь не найден", equalTo(exception.getMessage()));
    }

    @Test
    void findAllByStateForOwnerUnsupportedStateExceptionTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "INCORRECT";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);

        final var exception = assertThrows(
                EntityNotAvailableException.class,
                () -> bookingService.getOwnerBookingList(userId, from, size, state));

        assertThat("Unknown state: UNSUPPORTED_STATUS", equalTo(exception.getMessage()));
    }

    @Test
    void findAllByStateForOwnerAllTest() {
        final Long ownerId = owner.getId();
        final int from = 0;
        final int size = 10;
        final String state = "ALL";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);

        final var result = bookingService.getOwnerBookingList(ownerId, from, size, state);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findAllByStateForOwnerRejectedTest() {
        final Long ownerId = owner.getId();
        final int from = 0;
        final int size = 10;
        final String state = "REJECTED";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .build();
        bookingRepository.save(booking);

        final var result = bookingService.getOwnerBookingList(ownerId, from, size, state);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findAllByStateForOwnerFutureTest() {
        final Long ownerId = owner.getId();
        final int from = 0;
        final int size = 10;
        final String state = "FUTURE";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);

        final var result = bookingService.getOwnerBookingList(ownerId, from, size, state);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findAllByStateWaitingForOwnerTest() {
        final Long ownerId = owner.getId();
        final int from = 0;
        final int size = 10;
        final String state = "WAITING";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);

        final var result = bookingService.getOwnerBookingList(ownerId, from, size, state);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findAllByStateCurrentForOwnerTest() {
        final Long ownerId = owner.getId();
        final int from = 0;
        final int size = 10;
        final String state = "CURRENT";
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);

        final var result = bookingService.getOwnerBookingList(ownerId, from, size, state);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findAllByStatePastForOwnerTest() {
        final Long ownerId = owner.getId();
        final int from = 0;
        final int size = 10;
        final String state = "PAST";
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);

        final var result = bookingService.getOwnerBookingList(ownerId, from, size, state);

        assertThat(result.size(), equalTo(0));
    }

}

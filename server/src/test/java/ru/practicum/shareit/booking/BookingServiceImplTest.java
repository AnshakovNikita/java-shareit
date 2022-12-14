package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturnDto;
import ru.practicum.shareit.exception.EntityNotAvailableException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    private Booking booking;
    private User user;
    private static User booker;
    private Item item;

    private ItemDto itemDto;
    private static BookingDto bookingDto;

    private static BookingReturnDto bookingReturnDto;


    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(bookingRepository, itemService, userService);
        user = new User(1L, "User", "user@email.ru");
        booker = new User(2L, "Booker", "Booker@email.ru");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        item = new Item(1L, "Item", "Desc", true, itemRequest.getId(), user.getId());
        booking = new Booking(1L, BookingStatus.WAITING, booker, item, LocalDateTime.now(), LocalDateTime.now().plusMonths(2));

        bookingDto = new BookingDto(
                1L,
                booking.getStatus(),
                booking.getBooker().getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getName()
        );

        bookingReturnDto = new BookingReturnDto(
                1L,
                booking.getStatus(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem()),
                booking.getStart(),
                booking.getEnd()
        );

    }

    @Test
    void bookingAddFiledByEndInBeforeTest() {
        BookingDto failedBookingDto = new BookingDto(
                1L,
                booking.getStatus(),
                booking.getBooker().getId(),
                booking.getItem().getId(),
                booking.getStart().plusMonths(2),
                booking.getEnd().minusMonths(2),
                booking.getItem().getName()
        );

        Exception exc = assertThrows(IllegalStateException.class,
                () -> bookingService.addBooking(failedBookingDto, 2L));
        assertEquals("???????? ???????????? ???????????????????????? ???? ?????????? ???????? ?????????? ???????? ????????????????????", exc.getMessage());
    }

    @Test
    void patchBookingFailBookingTest() {
        Exception exc = assertThrows(EntityNotFoundException.class,
                () -> bookingService.patchBooking(100L, user.getId(), true));
        assertEquals("???????????????????????? ???? ??????????????", exc.getMessage());
    }

    @Test
    void findByIdTest() {
        final Long userId = 1L;
        final Long bookingId = 1L;

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        final var result = bookingService.getBooking(bookingId, userId);

        assertThat(result.getId(), equalTo(bookingReturnDto.getId()));
        assertThat(result.getStart(), equalTo(bookingReturnDto.getStart()));
        assertThat(result.getEnd(), equalTo(bookingReturnDto.getEnd()));
        assertThat(result.getItem(), equalTo(bookingReturnDto.getItem()));
        assertThat(result.getBooker().getId(), equalTo(bookingReturnDto.getBooker().getId()));
        assertThat(result.getStatus(), equalTo(bookingReturnDto.getStatus()));
    }

    @Test
    void patchBookingFailApprovedTest() {
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        Exception exc = assertThrows(EntityNotFoundException.class,
                () -> bookingService.patchBooking(booking.getId(), booker.getId(), true));
        assertEquals("???????????? ???????????????????????? ?????????? ???????????? ???????????? ???????????????? ????????", exc.getMessage());
    }

    @Test
    void patchBookingStatusApprovedTest() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        Exception exc = assertThrows(IllegalStateException.class,
                () -> bookingService.patchBooking(booking.getId(), user.getId(), true));
        assertEquals("???????????????????????? ?????? ????????????????????????", exc.getMessage());
    }

    @Test
    void getBookingFailBookingTest() {
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.empty());
        Exception exc = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(booking.getId(), user.getId()));
        assertEquals("???????????????????????? ???? ??????????????", exc.getMessage());
    }

    @Test
    void getBookingFailUserTest() {
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        Exception exc = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(booking.getId(), 100L));
        assertEquals("???????????????????????? ???? ???????????????? ???????????????????? ???????? ?????? ?????????????? ????????????????????????", exc.getMessage());
    }

    @Test
    void getUserBookingListUnknownStateTest() {
        Exception exc = assertThrows(EntityNotAvailableException.class,
                () -> bookingService.getUserBookingList(user.getId(), 0, 10, "UNKNOWN"));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exc.getMessage());
    }

    @Test
    void getOwnerBookingListUnknownStateTest() {
        Exception exc = assertThrows(EntityNotAvailableException.class,
                () -> bookingService.getOwnerBookingList(user.getId(), 0, 10, "UNKNOWN"));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exc.getMessage());
    }

    @Test
    void approveTest() {
        final Long userId = 1L;
        final Long bookingId = 1L;
        final Boolean approved = true;
        final Booking localBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .build();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(localBooking));
        when(bookingRepository.save(localBooking))
                .thenReturn(booking);

        final var result = bookingService.patchBooking(bookingId, userId, approved);

        assertThat(result.getId(), equalTo(bookingReturnDto.getId()));
        assertThat(result.getStart(), equalTo(bookingReturnDto.getStart()));
        assertThat(result.getEnd(), equalTo(bookingReturnDto.getEnd()));
        assertThat(result.getItem(), equalTo(bookingReturnDto.getItem()));
        assertThat(result.getBooker().getId(), equalTo(bookingReturnDto.getBooker().getId()));
        assertThat(result.getStatus(), equalTo(bookingReturnDto.getStatus()));
    }

    @Test
    void notApproveTest() {
        final Long userId = 1L;
        final Long bookingId = 1L;
        final Boolean approved = false;
        final Booking localBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(localBooking));
        when(bookingRepository.save(localBooking))
                .thenReturn(booking);

        final var result = bookingService.patchBooking(bookingId, userId, approved);

        assertThat(result.getId(), equalTo(bookingReturnDto.getId()));
        assertThat(result.getStart(), equalTo(bookingReturnDto.getStart()));
        assertThat(result.getEnd(), equalTo(bookingReturnDto.getEnd()));
        assertThat(result.getItem(), equalTo(bookingReturnDto.getItem()));
        assertThat(result.getBooker().getId(), equalTo(bookingReturnDto.getBooker().getId()));
        assertThat(result.getStatus(), equalTo(bookingReturnDto.getStatus()));
    }
}
package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturnDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {
    private static Booking booking;
    private static BookingDto bookingDto;
    private static BookingReturnDto bookingReturnDto;


    @BeforeAll
    static void beforeAll() {
        User user = new User(1L, "User", "user@email.ru");
        User booker = new User(2L, "Booker", "Booker@email.ru");
        Item item = new Item(1L, "Item", "Desc", true, user.getId(), null);
        booking = new Booking(1L, BookingStatus.WAITING,
                                 booker, item, LocalDateTime.now(), LocalDateTime.now().plusMonths(2));
        bookingDto = new BookingDto(
                booking.getId(),
                booking.getStatus(),
                booking.getBooker().getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getName()
        );

        bookingReturnDto = new BookingReturnDto(
                booking.getId(),
                booking.getStatus(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem()),
                booking.getStart(),
                booking.getEnd()
        );

    }

    @Test
    void toBookingReturnDtoTest() {
        BookingReturnDto result = BookingMapper.toBookingReturnDto(booking);
        assertEquals(bookingReturnDto.getId(), result.getId());
        assertEquals(bookingReturnDto.getStart(), result.getStart());
        assertEquals(bookingReturnDto.getEnd(), result.getEnd());
        assertEquals(bookingReturnDto.getBooker().getName(), result.getBooker().getName());
        assertEquals(bookingReturnDto.getItem().getName(), result.getItem().getName());
    }

    @Test
    void toBookingTest() {
        Booking result = BookingMapper.toBooking(bookingDto, booking.getItem(), booking.getBooker());
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getBooker().getName(), result.getBooker().getName());
        assertEquals(booking.getItem().getName(), result.getItem().getName());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
    }

    @Test
    void toBookingDtoTest() {
        BookingDto result = BookingMapper.toBookingDto(booking);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getBookerId(), result.getBookerId());
        assertEquals(bookingDto.getItemId(), result.getItemId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
    }

}
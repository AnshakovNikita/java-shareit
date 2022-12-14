package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    private BookingMapper() {
    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStatus(),
                booker,
                item,
                bookingDto.getStart(),
                bookingDto.getEnd());
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStatus(),
                booking.getBooker().getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getName());
    }

    public static BookingReturnDto toBookingReturnDto(Booking booking) {
        return new BookingReturnDto(booking.getId(),
                booking.getStatus(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem()),
                booking.getStart(),
                booking.getEnd());
    }
}
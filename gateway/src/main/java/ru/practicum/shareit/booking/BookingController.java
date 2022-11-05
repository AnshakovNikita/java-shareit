package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingReturnDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.EntityNotAvailableException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping()
    protected ResponseEntity<Object> addBookingGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @Valid @RequestBody BookingReturnDto bookingDto) {
        log.info("Post-запрос на добавление бронирования {}", bookingDto);
        return bookingClient.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    protected ResponseEntity<Object> patchBookingGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PathVariable Long bookingId,
                                                         @RequestParam(value = "approved") Boolean approved) {
        log.info("Patch-запрос на изменение бронирования. id пользователя = {}, id бронирования = {}, статус = {}",
                userId, bookingId, approved);
        return bookingClient.patchBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    protected ResponseEntity<Object> getBookingGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @PathVariable(value = "bookingId", required = false)
                                                           Long bookingId) {
        log.info("Get-запрос на получение бронирования. id бронирования {}, id ползователя {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping()
    protected ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                             @Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                             @RequestParam(value = "state", defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new EntityNotAvailableException("Unknown state: UNSUPPORTED_STATUS"));
        log.info("Get-запрос на получение списка бронирований пользователя с id {} и статусом {}", userId, state);
        return bookingClient.getUserBookings(userId, from, size, bookingState);
    }

    @GetMapping("/owner")
    protected ResponseEntity<Object> getOwnerBookingsGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                  @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                                  @Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                  @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new EntityNotAvailableException("Unknown state: UNSUPPORTED_STATUS"));
        log.info("Get-запрос на получение списка бронирований пользователя с id {} и статусом {}", userId, state);
        return bookingClient.getOwnerBookings(userId, from, size, bookingState);
    }
}
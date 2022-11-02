package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.validation.EndNotInPast;
import ru.practicum.shareit.validation.StartNotInPast;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private BookingStatus status;
    private Long bookerId;
    private Long itemId;
    @StartNotInPast
    private LocalDateTime start;
    @EndNotInPast
    private LocalDateTime end;
    private String itemName;
}
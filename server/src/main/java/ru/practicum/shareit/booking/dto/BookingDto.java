package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private BookingStatus status;
    private Long bookerId;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private String itemName;
}
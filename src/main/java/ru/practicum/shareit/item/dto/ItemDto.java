package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
public class ItemDto {
    private final long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private User owner;
    private ItemRequest request;
}
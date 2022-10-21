package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Item item, long userId);

    ItemDto updateItem(Item updatedItem, long itemId, long userId);

    ItemDto getById(long itemId);

    List<ItemDto> searchItem(String text);

    List<ItemDto> getItems(long userId);
}
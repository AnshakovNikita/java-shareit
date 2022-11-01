package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto updatedItemDto, long itemId, long userId);

    ItemDto getById(Long itemId, Long userId);

    Item getItemById(Long itemId);

    List<ItemDto> searchItem(String text, Integer from, Integer size);

    List<ItemDto> getAllItemsUser(Long userId, Integer from, Integer size);

    List<ItemDto> getItems(long userId);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
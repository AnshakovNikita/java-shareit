package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Set;

public interface ItemRepository {

    Item add(Item item, long userId);

    Item getById(long itemId);

    Collection<Item> findAll();

    Set<Long> getUserItems(long userId);
}
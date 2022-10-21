package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final ConcurrentHashMap<Long, Item> items = new ConcurrentHashMap<>();
    private final HashMap<Long, Set<Long>> userItems = new HashMap<>();

    private long startId;

    @Override
    public Collection<Item> findAll() {
        log.info("Получены все предметы = {}", items.values());
        return  items.values();
    }

    @Override
    public Item getById(long itemId) {
        log.info("Получен предмет с id = {}", itemId);
        return items.get(itemId);
    }

    @Override
    public Item add(Item item, long userId) {
        Set<Long> itemsOfUser;
        if (userItems.get(userId) != null) {
            itemsOfUser = userItems.get(userId);
        } else {
            itemsOfUser = new HashSet<>();
        }
        if (item.getId() == 0) {
            item.setId(++startId);
        }
        items.put(item.getId(), item);
        log.info("Добавлен предмет = {}", item);

        itemsOfUser.add(item.getId());
        userItems.put(userId, itemsOfUser);
        log.info("Добавлен предмет с id = {}  пользователю с id = {} ", item.getId(), userId);
        return items.get(item.getId());
    }

    @Override
    public Set<Long> getUserItems(long userId) {
        return userItems.get(userId);
    }


}
package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserService userService;

    @Override
    public ItemDto addItem(Item item, long userId) {
        if (userService.getUserById(userId) == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return ItemMapper.toItemDto(itemRepository.add(item, userId));
    }

    @Override
    public ItemDto updateItem(Item updatedItem, long itemId, long userId) {
        Item item = itemRepository.getById(itemId);
        Set<Long> userItems = itemRepository.getUserItems(userId);
        if (userItems == null || !userItems.contains(itemId)) {
            throw new EntityNotFoundException("Предмет отсутсвует у данного пользователя");
        }
        if (item == null) {
            throw new EntityNotFoundException("Товар не найден");
        }
        if (userService.getUserById(userId) == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        if (StringUtils.hasText(updatedItem.getName())) {
            item.setName(updatedItem.getName());
        }
        if (StringUtils.hasText(updatedItem.getDescription())) {
            item.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getAvailable() != null) {
            item.setAvailable(updatedItem.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.add(item, userId));
    }


    @Override
    public ItemDto getById(long itemId) {
        return ItemMapper.toItemDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (!StringUtils.hasText(text)) {
            return new ArrayList<>();
        }
        Stream<Item> findByName = itemRepository.findAll().stream()
                .filter((Item item) -> item.getName().toLowerCase().contains(text.toLowerCase()));
        Stream<Item> findByDescription = itemRepository.findAll().stream()
                .filter((Item item) -> item.getDescription().toLowerCase().contains(text.toLowerCase()));
        return Stream.concat(findByDescription, findByName)
                .distinct()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        if (userId == 0) {
            return getAllItems();
        } else {
            if (userService.getUserById(userId) == null) {
                throw new EntityNotFoundException("Пользователь не найден");
            }
            return getItemByUser(userId);
        }
    }

    private List<ItemDto> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private List<ItemDto> getItemByUser(long userId) {
        return itemRepository.getUserItems(userId)
                .stream()
                .map(itemRepository::getById)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
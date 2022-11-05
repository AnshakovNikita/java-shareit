package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private Item item;
    private ItemDto itemDto;
    private List<Item> list;


    @BeforeEach
    void beforeEach() {
        User user1 = new User(1L, "user1", "user1@mail.com");
        User user2 = new User(2L, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", user1.getId(), localDateTime.minusMonths(2));
        item = new Item(1L,"item","desc",true, itemRequest.getId(), user2.getId());

        itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getRequestId());

        list = Collections.singletonList(item);
    }

    @Test
    void toItemDtoTest() {
        ItemDto result = ItemMapper.toItemDto(item);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(itemDto.getRequestId(), result.getRequestId());
    }

    @Test
    void toItemTest() {
        Item result = ItemMapper.toItem(itemDto);
        assertEquals(result.getId(), itemDto.getId());
        assertEquals(result.getName(), itemDto.getName());
        assertEquals(result.getDescription(), itemDto.getDescription());
        assertEquals(result.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void toListItemDtoTest() {
        List<ItemDto> result = ItemMapper.toItemDtoList(list);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), 1L);
    }
}
package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.FromSizeRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;


    @Transactional
    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toItem(itemDto);

        if (userService.getUserById(userId) == null)
            throw new EntityNotFoundException("Пользователь не найден");

        item.setUserId(userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto updatedItemDto, long itemId, long userId) {
        if (userService.getUserById(userId) == null)
            throw new EntityNotFoundException("Пользователь не найден");
        Item updatedItem = ItemMapper.toItem(updatedItemDto);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new EntityNotFoundException("Товар не найден");
        });
        Set<Long> userItems = userService.getUserItems(userId).stream()
                .map(Item::getId)
                .collect(Collectors.toSet());
        if (!userItems.contains(itemId)) {
            throw new EntityNotFoundException("Предмет отсутсвует у данного пользователя");
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
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new EntityNotFoundException("Товар не найден");
        });
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(commentRepository.findCommentsByItemOrderByCreatedDesc(item)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        if (userId.equals(item.getUserId())) {
            return setLastAndNextBookingForItem(itemDto);
        } else {
            return itemDto;
        }
    }

    @Override
    public Item getItemById(Long itemId) {
        System.out.println(itemRepository.findById(itemId) + "111");
        return itemRepository.findById(itemId).orElseThrow(() -> {
            throw new EntityNotFoundException("Товар не найден");
        });
    }

    @Override
    public List<ItemDto> searchItem(String text, Integer from, Integer size) {
        Pageable pageable = FromSizeRequest.of(from, size);
        if (!StringUtils.hasText(text)) {
            return new ArrayList<>();
        }

        return itemRepository.searchItem(text, pageable)
                .stream()
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

    @Override
    public List<ItemDto> getAllItemsUser(Long userId, Integer from, Integer size) {

        if (userService.getUserById(userId) == null)
            throw new EntityNotFoundException("Пользователь не найден");

        Pageable pageable = FromSizeRequest.of(from, size);
        List<Item> items = itemRepository.findAllByUserIdOrderByIdAsc(userId, pageable);
        List<ItemDto> result = new ArrayList<>();

        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            result.add(setLastAndNextBookingForItem(itemDto));
        }

        return result;
    }

    private List<ItemDto> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private List<ItemDto> getItemByUser(long userId) {
        return userService.getUserItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .map((this::setLastAndNextBookingForItem))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        List<Booking> bookings = bookingRepository
                .findBookingsByBookerAndItemAndStatusNot(userId, itemId, BookingStatus.REJECTED);
        if (bookings.isEmpty()) {
            throw new IllegalStateException("У предмета не было бронирований");
        }
        boolean future = true;
        for (Booking booking : bookings) {
            if (booking.getEnd().isBefore(LocalDateTime.now())) {
                future = false;
                break;
            }
        }
        if (future) {
            throw new IllegalStateException("Комментарий не может быть оставлен к будущему бронированию");
        }
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(getItemById(itemId));
        comment.setAuthor(UserMapper.toUser(userService.getUserById(userId)));
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private ItemDto setLastAndNextBookingForItem(ItemDto itemDto) {
        Booking lastBooking = null;
        Booking nextBooking = null;
        List<Booking> bookings = bookingRepository.findBookingsByItemAsc(itemDto.getId());
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            if (booking.getStart().isAfter(LocalDateTime.now())) {
                nextBooking = booking;
                if (i != 0) {
                    lastBooking = bookings.get(i - 1);
                }
                break;
            }
        }
        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.toBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.toBookingDto(nextBooking));
        }
        return itemDto;
    }
}
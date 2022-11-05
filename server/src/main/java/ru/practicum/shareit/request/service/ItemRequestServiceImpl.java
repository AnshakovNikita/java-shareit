package ru.practicum.shareit.request.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotAvailableException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.FromSizeRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        if (!userRepository.existsById(userId))
            throw new EntityNotFoundException("Пользователь не найден");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequesterId(userId);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllByRequester(Long requesterId) {
        if (!userRepository.existsById(requesterId))
            throw new EntityNotFoundException("Пользователь не найден");

        List<ItemRequestDto> requests = ItemRequestMapper.toItemRequestDtoList(
                itemRequestRepository.findByRequesterIdIsOrderByCreatedDesc(requesterId)
        );
        for (ItemRequestDto itemRequestDto : requests) {
            itemRequestDto.setItems(ItemMapper.toItemDtoList(
                    itemRepository.findAllByRequestId(itemRequestDto.getId())));
        }
        return requests;
    }

    @Override
    public List<ItemRequestDto> findAll(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId))
            throw new EntityNotAvailableException("Пользователь не найден");
        Pageable pageable = FromSizeRequest.of(from, size);
        List<ItemRequestDto> itemRequestDto = ItemRequestMapper.toItemRequestDtoList(
                itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageable).getContent()
        );
        for (ItemRequestDto requestDto : itemRequestDto) {
            List<ItemDto> items = ItemMapper.toItemDtoList(itemRepository.findAllByRequestId(requestDto.getId()));
            requestDto.setItems(items);
        }
        return itemRequestDto;
    }

    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId))
            throw new EntityNotFoundException("Пользователь не найден");
        if (!itemRequestRepository.existsById(requestId))
            throw new EntityNotFoundException("Запрос не найден");

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос не существует")));

        List<ItemDto> items = ItemMapper.toItemDtoList(itemRepository.findAllByRequestId(requestId));
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

}

package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findAllByRequester(Long ownerId);

    List<ItemRequestDto> findAll(Long ownerId, Integer from, Integer size);

    ItemRequestDto findById(Long userId, Long requestId);
}
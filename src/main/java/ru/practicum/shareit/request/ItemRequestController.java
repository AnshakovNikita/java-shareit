package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание Request пользователя {}", userId);
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemRequestDto> findAllByRequester(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Получен запрос на вывод своих Requests {}", requesterId);
        return itemRequestService.findAllByRequester(requesterId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable(value = "requestId") Long requestId) {
        log.info("Получен запрос на вывод Request с id = {} пользователя с id = {}", requestId, userId);
        return itemRequestService.findById(userId, requestId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemRequestDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                      @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос на вывод списка Requests пользователя {}", userId);
        return itemRequestService.findAll(userId, from, size);
    }
}
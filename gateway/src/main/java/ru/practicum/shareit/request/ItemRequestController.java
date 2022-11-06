package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.ValidationException;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping()
    protected ResponseEntity<Object> createItemRequestGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание Request пользователя {}", userId);
        return requestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping()
    protected ResponseEntity<Object> findAllByRequesterGateWay(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("Получен запрос на вывод своих Requests {}", requesterId);
        return requestClient.findAllByRequester(requesterId);
    }

    @GetMapping("/{requestId}")
    protected ResponseEntity<Object> getItemRequestGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @PathVariable Long requestId) {
        return requestClient.findById(userId, requestId);
    }

    @GetMapping("/all")
    protected ResponseEntity<Object> findAllGateWay(@RequestHeader("X-Sharer-User-Id") Long idUser,
                                                    @RequestParam(value = "from", defaultValue = "0")  Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") Integer size)
            throws ValidationException {
        return requestClient.findAll(idUser, from, size);
    }
}

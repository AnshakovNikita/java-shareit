package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping()
    protected ResponseEntity<Object> addItemGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление предмета {} пользователем с id {}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    protected ResponseEntity<Object> updateItemGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PathVariable Long id,
                                                       @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на обновление предмета = {} пользователем с id = {}", itemDto, userId);
        return itemClient.updateItem(userId, itemDto, id);
    }

    @GetMapping()
    protected ResponseEntity<Object> getUserItemsGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение списка предметов пользователя с id = {}", userId);
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/{id}")
    protected ResponseEntity<Object> getItemByIdGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @PathVariable Long id) {
        log.info("Получен запрос на получение предмета с id = {}", id);
        return itemClient.getItemById(userId, id);
    }

    @GetMapping("/search")
    protected ResponseEntity<Object> searchItemGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam("text") String text,
                                                       @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("ItemController.getItemByIdSearchGateWay, userId = {}, text = {}", userId, text);
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    protected ResponseEntity<Object> addCommentGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PathVariable Long itemId,
                                                       @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен запрос на добавление комментария {} к предмету {} " +
                "пользователем с id {}", commentDto, itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}

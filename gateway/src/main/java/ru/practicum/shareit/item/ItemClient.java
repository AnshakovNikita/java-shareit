package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Slf4j
@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(Long userId, ItemDto itemDto) {
        log.info("Получен запрос на добавление предмета {} пользователем с id {}", itemDto, userId);
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, ItemDto itemDto, Long id) {
        log.info("Получен запрос на обновление предмета = {} пользователем с id = {}", itemDto, userId);
        return patch("/" + id, userId, itemDto);
    }

    public ResponseEntity<Object> getUserItems(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        log.info("Получен запрос на получение списка предметов пользователя с id = {}", userId);
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long id) {
        log.info("Получен запрос на получение предмета с id = {}", id);
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> searchItem(Long userId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        log.info("Получен запрос на поиск предмета по тексту - {}", text);
        return get("/search" + "?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Получен запрос на добавление комментария {} к предмету {} " +
                "пользователем с id {}", commentDto, itemId, userId);
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
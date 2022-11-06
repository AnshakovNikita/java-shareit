package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Slf4j
@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание Request пользователя {}", userId);
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> findAllByRequester(Long requesterId) {
        log.info("Получен запрос на вывод своих Requests {}", requesterId);
        return get("", requesterId);
    }

    public ResponseEntity<Object> findById(Long userId, Long requestId) {
        log.info("Получен запрос на вывод Request с id = {} пользователя с id = {}", requestId, userId);
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> findAll(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        log.info("Получен запрос на вывод списка Requests пользователя {}", userId);
        return get("/all?from={from}&size={size}", userId, parameters);
    }
}

package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> postUser(UserDto userDto) {
        log.info("UserClient.createUser, userDto = {} ", userDto);
        return post("", userDto);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        log.info("UserClient.getUser user id = {} ", userId);
        return get("/" + userId, userId);
    }

    public ResponseEntity<Object> patchUser(UserDto userDto, Long userId) {
        log.info("UserClient.updateUser user id = {}, userDto = {} ", userId, userDto);
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> deleteUserById(Long userId) {
        log.info("UserClient.deleteUser user id = {} ", userId);
        return delete("/" + userId, userId);
    }

    public ResponseEntity<Object> getAllUser() {
        log.info("UserClient.getAllUser");
        return get("");
    }
}
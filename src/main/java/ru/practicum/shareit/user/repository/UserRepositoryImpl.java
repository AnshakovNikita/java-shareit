package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private long startId;

    @Override
    public Collection<User> findAll() {
        log.info("Получены все пользователи = {}", users.values());
        return  users.values();
    }

    @Override
    public User getById(long userId) {
        log.info("Получен пользователь c id = {}", userId);
        return users.get(userId);
    }

    @Override
    public User add(User user) {
        if (user.getId() == 0) {
            user.setId(++startId);
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь = {}", user);
        return getById(user.getId());
    }

    @Override
    public void delete(long userId) {
        log.info("Удалён пользователь c id = {}", userId);
        users.remove(userId);
    }
}
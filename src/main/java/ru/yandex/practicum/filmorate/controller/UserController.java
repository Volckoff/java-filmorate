package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Попытка создания пользователя: {}", user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан: {}", user);
        return user;
    }

    private int getNextId() {
        return users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Попытка обновления пользователя: {}", user);
        Integer id = user.getId();
        if (!users.containsKey(id)) {
            log.warn("Ошибка обновления пользователя: пользователь с ID {} не найден", id);
            throw new ValidationException("Пользователь с таким ID не найден");
        }
        user.setName(user.getName() == null || user.getName().isBlank() ? user.getLogin() : user.getName());
        users.put(id, user);
        log.info("Пользователь успешно обновлен: {}", user);
        return user;
    }

}

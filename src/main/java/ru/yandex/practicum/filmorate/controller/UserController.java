package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.Update;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        log.info("Запрос на получение пользователя с ID {}", id);
        Optional<User> userOptional = userService.getById(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("Error", "Not found", "Message", "Пользователь с ID "
                            + id + " не найден."));
        }
    }


    @PostMapping
    public User createUser(@Validated(Create.class) @RequestBody User user) {
        log.info("Попытка создания пользователя: {}", user);
        return userService.createUser(user);
    }


    @PutMapping
    public User updateUser(@Validated(Update.class) @RequestBody User user) {
        log.info("Попытка обновления пользователя: {}", user);
        return userService.updateUser(user);
    }


    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Добавление друга с ID {} пользователю с ID {}", friendId, id);
        userService.addFriend(id, friendId);
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Удаление друга с ID {} у пользователя с ID {}", friendId, id);
        userService.removeFriend(id, friendId);
    }


    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Запрос на получение списка друзей пользователя с ID {}", id);
        return userService.getFriends(id);
    }


    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Запрос на получение общих друзей пользователей с ID {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}

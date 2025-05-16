package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.Update;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userService.getAll();
    }


    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable @Positive(message = "ID должен быть больше нуля") int id) {
        log.info("Запрос на получение пользователя с ID {}", id);
        return userService.getById(id);
    }


    @PostMapping
    @Validated(Create.class)
    public UserDto createUser(@RequestBody @Valid NewUserRequest request) {
        log.info("Попытка создания пользователя: {}", request);
        return userService.createUser(request);
    }


    @PutMapping
    @Validated(Update.class)
    public UserDto updateUser(@RequestBody @Valid UpdateUserRequest request) {
        return userService.updateUser(request);
    }


    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable @Positive(message = "ID пользователя должен быть положительным") int id,
            @PathVariable @Positive(message = "ID друга должен быть положительным") int friendId) {
        log.info("Добавление друга с ID {} пользователю с ID {}", friendId, id);
        userService.addFriend(id, friendId);
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable @Positive(message = "ID пользователя должен быть положительным") int id,
            @PathVariable @Positive(message = "ID друга должен быть положительным") int friendId) {
        log.info("Удаление друга с ID {} у пользователя с ID {}", friendId, id);
        userService.removeFriend(id, friendId);
    }


    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(
            @PathVariable @Positive(message = "ID пользователя должен быть положительным") int id) {
        log.info("Запрос на получение списка друзей пользователя с ID {}", id);
        return userService.getFriends(id);
    }


    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(
            @PathVariable @Positive(message = "ID пользователя должен быть положительным") int id,
            @PathVariable @Positive(message = "ID другого пользователя должен быть положительным") int otherId) {
        log.info("Запрос на получение общих друзей для пользователей с ID {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}

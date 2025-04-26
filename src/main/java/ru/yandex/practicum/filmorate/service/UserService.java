package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;


    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User updatedUser) {
        Integer id = updatedUser.getId();
        Optional<User> existingUserOptional = userStorage.getById(id);
        if (existingUserOptional.isEmpty()) {
            log.warn("Ошибка обновления пользователя: пользователь с ID {} не найден", id);
            throw new NotFoundException("Пользователь с таким ID не найден");
        }
        User existingUser = existingUserOptional.get();
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getLogin() != null && !updatedUser.getLogin().isEmpty()) {
            existingUser.setLogin(updatedUser.getLogin());
        }
        if (updatedUser.getName() != null && !updatedUser.getName().isEmpty()) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getBirthday() != null) {
            existingUser.setBirthday(updatedUser.getBirthday());
        }
        log.info("Пользователь успешно обновлен: {}", existingUser);
        return userStorage.updateUser(existingUser);
    }


    public User getById(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден."));
    }


    public List<User> getAll() {
        return userStorage.getAll();
    }


    public void addFriend(int userId, int friendId) {
        User user = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        User friend = userStorage.getById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден."));
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }


    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        User friend = userStorage.getById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден."));
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }


    public List<User> getFriends(int id) {
        return userStorage.getById(id)
                .map(user -> user.getFriends().stream()
                        .map(userStorage::getById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList())
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден."));
    }


    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        User other = userStorage.getById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + otherId + " не найден."));
        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(userStorage::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}

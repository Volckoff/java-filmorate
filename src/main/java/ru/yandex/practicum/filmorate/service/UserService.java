package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserDbStorage userDbStorage;
    private final UserMapper userMapper;

    public UserService(@Qualifier("userDbStorage") UserDbStorage userDbStorage,
                       UserMapper userMapper) {
        this.userDbStorage = userDbStorage;
        this.userMapper = userMapper;
    }

    public UserDto createUser(NewUserRequest request) {
        User user = userMapper.mapToUser(request);
        return userMapper.mapToUserDto(userDbStorage.createUser(user));
    }


    public UserDto updateUser(UpdateUserRequest request) {
        User existingUser = userDbStorage.getById(request.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        existingUser = userMapper.updateFromRequest(existingUser, request);
        existingUser = userDbStorage.updateUser(existingUser);
        return userMapper.mapToUserDto(existingUser);
    }

    public List<UserDto> getAll() {
        return userDbStorage.getAll().stream()
                .map(userMapper::mapToUserDto)
                .toList();
    }

    public UserDto getById(Integer id) {
        return userDbStorage.getById(id)
                .map(userMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public void addFriend(Integer userId, Integer friendId) {
        log.info("Попытка добавления в друзья у пользователя с ID: {}", userId);
        User user = userDbStorage.getById(userId).get();
        User friend = userDbStorage.getById(friendId).get();
        user.getFriends().add(friendId);
        userDbStorage.addFriend(userId, friendId);
        log.info("Добавлен друг у пользователя с ID: {}", userId);
    }


    public void removeFriend(Integer userId, Integer friendId) {
        User user = userDbStorage.getById(userId).get();
        User friend = userDbStorage.getById(friendId).get();
        userDbStorage.removeFriend(userId, friendId);
        log.info("Пользователь с ID {} перестал дружить с пользователем с ID {}", userId, friendId);
    }

    public List<UserDto> getFriends(Integer id) {
        userDbStorage.getById(id); //проверка что такой юзер существует
        List<User> friends = userDbStorage.getFriends(id);
        return friends.stream()
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getCommonFriends(Integer userId1, Integer friendId) {
        log.info("Попытка получения общих друзей у пользователя с ID: {}", userId1);
        List<Integer> friendsOfUser1 = userDbStorage.getFriendsId(userId1);
        List<Integer> friendsOfUser2 = userDbStorage.getFriendsId(friendId);
        Set<Integer> commonFriends = new HashSet<>(friendsOfUser1);
        commonFriends.retainAll(friendsOfUser2);
        return commonFriends.stream()
                .map(userDbStorage::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}

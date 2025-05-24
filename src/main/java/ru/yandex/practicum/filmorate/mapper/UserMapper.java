package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Component
public class UserMapper {

    public RowMapper<User> mapRow() {
        return (rs, rowNum) -> new User(
                rs.getObject("user_id", Integer.class),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getObject("birthday", LocalDate.class)
        );
    }

    public User mapToUser(NewUserRequest request) {
        return new User(
                null,
                request.getEmail(),
                request.getLogin(),
                request.getName(),
                request.getBirthday()
        );
    }

    public User updateFromRequest(User existing, UpdateUserRequest request) {
        if (request.hasEmail()) existing.setEmail(request.getEmail());
        if (request.hasLogin()) existing.setLogin(request.getLogin());
        if (request.hasName()) existing.setName(request.getName());
        if (request.hasBirthday()) existing.setBirthday(request.getBirthday());
        return existing;
    }

    public UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setName(user.getName());
        dto.setBirthday(user.getBirthday());
        dto.setFriends(user.getFriends());
        return dto;
    }
}

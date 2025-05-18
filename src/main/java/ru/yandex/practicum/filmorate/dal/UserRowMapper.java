package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


@Component
public class UserRowMapper implements RowMapper<User> {
    private final Map<Integer, User> userMap = new HashMap<>();

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        int userId = rs.getInt("user_id");

        User user = userMap.get(userId);
        if (user == null) {
            user = new User();
            user.setId(userId);
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getObject("birthday", LocalDate.class));
            user.setFriends(new LinkedHashSet<>());
            userMap.put(userId, user);
        }

        Integer friendId = rs.getObject("friend_id", Integer.class);
        if (friendId != null) {
            user.getFriends().add(friendId);
        }

        return user;
    }

    public List<User> getMappedUsers() {
        return new ArrayList<>(userMap.values());
    }
}

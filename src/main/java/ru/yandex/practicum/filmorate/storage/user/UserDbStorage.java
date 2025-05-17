package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private static final String FIND_ALL_USERS_SQL = "SELECT * FROM users";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_USER_SQL = """
            INSERT INTO users(email, login, name, birthday)
            VALUES (?, ?, ?, ?)
            """;
    private static final String UPDATE_USER_SQL = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String ADD_FRIEND_SQL = "INSERT INTO friendship(user_id, friend_id) VALUES (?, ?)";
    private static final String REMOVE_FRIEND_SQL = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS_SQL = "SELECT friend_id FROM friendship WHERE user_id = ?";
    private static final String GET_COMMON_FRIENDS_SQL =
            "SELECT u.* FROM users AS u JOIN friendship AS f ON u.id = f.friend_id WHERE f.user_id = ? " +
                    "JOIN friendship f1 ON u.id = f1.friend_id AND f1.user_id = ? " +
                    "JOIN friendship f2 ON u.id = f2.friend_id AND f2.user_id = ?";


    @Override
    public User createUser(User user) {
        Integer id = insert(INSERT_USER_SQL,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        user.setId(id);
        return user;
    }


    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(UPDATE_USER_SQL,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }


    @Override
    public Optional<User> getById(int id) {
        try {
            User user = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, new Object[]{id}, new UserRowMapper());
            if (user == null) {
                throw new NotFoundException("Пользователь c таким id не найден: " + id);
            }
            user.setFriends(getFriendId(user.getId()));
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
    }


    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query(FIND_ALL_USERS_SQL, new UserRowMapper());
        for (User user : users) {
            user.setFriends(getFriendId(user.getId()));
        }
        return users;
    }


    public void addFriend(int userId, int friendId) {
        jdbcTemplate.update(ADD_FRIEND_SQL, userId, friendId);
    }


    public void removeFriend(int userId, int friendId) {
        jdbcTemplate.update(REMOVE_FRIEND_SQL, userId, friendId);
    }

    public List<Integer> getFriendsId(int userId) {
        List<Integer> friendsList = jdbcTemplate.queryForList(GET_FRIENDS_SQL, new Object[]{userId}, Integer.class);
        return friendsList;
    }


    public List<User> getFriends(int userId) {
        List<Integer> friendsList = jdbcTemplate.queryForList(GET_FRIENDS_SQL, new Object[]{userId}, Integer.class);
        List<User> friends = new ArrayList<>();
        for (Integer id : friendsList) {
            User user = getById(id).get();
            friends.add(user);
        }
        return friends;
    }


    public List<User> getCommonFriends(int userId, int otherId) {
        return jdbcTemplate.query(
                GET_COMMON_FRIENDS_SQL,
                new BeanPropertyRowMapper<>(User.class),
                userId, otherId
        );
    }


    private Integer insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId == null) {
            throw new InternalServerException("Не удалось получить ID после вставки");
        }
        return generatedId.intValue();
    }


    public Set<Integer> getFriendId(int userId) {
        List<Integer> friendsList = jdbcTemplate.queryForList(GET_FRIENDS_SQL, new Object[]{userId}, Integer.class);
        return new HashSet<>(friendsList);
    }
}

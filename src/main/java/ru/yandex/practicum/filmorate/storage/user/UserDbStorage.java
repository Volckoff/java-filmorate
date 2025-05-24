package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


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
    private static final String GET_All_USERS = "SELECT u.user_id, u.email, u.login, " +
            "u.name, u.birthday, f.friend_id " +
            "FROM users u " +
            "LEFT JOIN friendship f ON u.user_id = f.user_id";
    private static final String GET_USER_BY_ID = "SELECT u.user_id, u.email, u.login, " +
            "u.name, u.birthday, f.friend_id " +
            "FROM users u " +
            "LEFT JOIN friendship f ON u.user_id = f.user_id " +
            "WHERE u.user_id = ?";
    private static final String GET_FRIENDS_FULL_SQL = """
            SELECT * FROM users AS u
            INNER JOIN friendship AS uf ON u.user_id = uf.friend_id WHERE uf.user_id = ?""";


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
        UserRowMapper rowMapper = new UserRowMapper();
        jdbcTemplate.query(GET_USER_BY_ID, rowMapper::mapRow, id);
        return Optional.ofNullable(Optional.of(rowMapper.getMappedUsers())
                .filter(users -> !users.isEmpty())
                .map(users -> users.get(0))
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден")));
    }


    @Override
    public List<User> getAll() {
        UserRowMapper rowMapper = new UserRowMapper();
        jdbcTemplate.query(GET_All_USERS, rowMapper::mapRow);
        return rowMapper.getMappedUsers();
    }


    public void addFriend(int userId, int friendId) {
        jdbcTemplate.update(ADD_FRIEND_SQL, userId, friendId);
    }


    public void removeFriend(int userId, int friendId) {
        jdbcTemplate.update(REMOVE_FRIEND_SQL, userId, friendId);
    }


    public Set<User> getFriends(int userId) {
        List<User> friends = jdbcTemplate.query(GET_FRIENDS_FULL_SQL, new UserRowMapper(), userId);
        return new HashSet<>(friends);
    }


    public List<User> getCommonFriends(int userId, int otherId) {
        return jdbcTemplate.query(
                GET_COMMON_FRIENDS_SQL,
                new UserRowMapper(),
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
}

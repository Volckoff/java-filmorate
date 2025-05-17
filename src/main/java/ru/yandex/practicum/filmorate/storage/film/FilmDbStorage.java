package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    private static final String FIND_ALL_FILMS_SQL = "SELECT * FROM films";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM films WHERE film_id = ?";
    private static final String INSERT_FILM_SQL = """
            INSERT INTO films(name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_FILM_SQL = """
            UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE film_id = ?
            """;
    private static final String ADD_LIKE_SQL = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_SQL = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_LIKES_FOR_FILM_SQL = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String GET_POPULAR_FILMS_SQL = """
            SELECT f.*, COUNT(l.user_id) AS like_count
            FROM films f
            LEFT JOIN likes l ON f.film_id = l.film_id
            GROUP BY f.film_id
            ORDER BY like_count DESC
            LIMIT ?
            """;
    private static final String DELETE_GENRES_FOR_FILM_SQL = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String ADD_GENRE_TO_FILM_SQL = """
            INSERT INTO film_genre(film_id, genre_id)
            VALUES (?, ?)
            """;


    @Override
    public Film createFilm(Film film) {
        Integer id = insert(INSERT_FILM_SQL,
                true,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Integer> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            addGenres(id, genreIds);
        }
        return film;
    }


    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(
                UPDATE_FILM_SQL,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        jdbcTemplate.update(DELETE_GENRES_FOR_FILM_SQL, film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Integer> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .toList();
            List<Object[]> batch = new ArrayList<>();
            for (Integer genreId : genreIds) {
                batch.add(new Object[]{film.getId(), genreId});
            }
            jdbcTemplate.batchUpdate(ADD_GENRE_TO_FILM_SQL, batch);
        }
        return film;
    }


    @Override
    public Optional<Film> getById(int id) {
        try {
            Film film = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, filmRowMapper, id);
            film.setLikes(getLikesForFilm(id));
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }


    @Override
    public List<Film> getAll() {
        List<Film> films = jdbcTemplate.query(FIND_ALL_FILMS_SQL, filmRowMapper);
        for (Film film : films) {
            film.setLikes(getLikesForFilm(film.getId()));
        }
        return films;
    }


    public void addLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(ADD_LIKE_SQL, filmId, userId);
    }


    public void removeLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(REMOVE_LIKE_SQL, filmId, userId);
    }


    public List<Film> getPopular(int count) {
        return jdbcTemplate.query(GET_POPULAR_FILMS_SQL, filmRowMapper, count);
    }


    protected Integer insert(String query, boolean expectGeneratedKey, Object... params) {
        if (expectGeneratedKey) {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
                return ps;
            }, keyHolder);

            return keyHolder.getKey().intValue();
        } else {
            int rowsCreated = jdbcTemplate.update(query, params);
            if (rowsCreated == 0) {
                throw new RuntimeException("Не удалось выполнить запрос к БД");
            }
            return rowsCreated;
        }
    }


    public void addGenres(Integer filmId, Set<Integer> genreIds) {
        jdbcTemplate.update(DELETE_GENRES_FOR_FILM_SQL, filmId);
        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }
        List<Object[]> batch = genreIds.stream()
                .map(genreId -> new Object[]{filmId, genreId})
                .toList();
        jdbcTemplate.batchUpdate(ADD_GENRE_TO_FILM_SQL, batch);
    }


    public Set<Integer> getLikesForFilm(Integer filmId) {
        return new HashSet<>(jdbcTemplate.queryForList(GET_LIKES_FOR_FILM_SQL, Integer.class, filmId));
    }
}
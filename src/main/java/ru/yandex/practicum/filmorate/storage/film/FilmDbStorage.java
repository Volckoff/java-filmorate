package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
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
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name,
             g.genre_id, g.name AS genre_name
            FROM films f
            LEFT JOIN likes l ON f.film_id = l.film_id
            LEFT JOIN mpa_rating m ON f.mpa_id = m.mpa_id
            LEFT JOIN film_genre fg ON f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name, g.genre_id, g.name
            ORDER BY COUNT(l.user_id) DESC
            LIMIT ?
            """;
    private static final String DELETE_GENRES_FOR_FILM_SQL = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String ADD_GENRE_TO_FILM_SQL = """
            INSERT INTO film_genre(film_id, genre_id)
            VALUES (?, ?)
            """;
    private static final String FIND_FILM_WITH_GENRES_SQL = """
            SELECT f.film_id, f.name AS film_name, f.description,
                f.release_date, f.duration, f.mpa_id, m.name AS mpa_name,
                g.genre_id, g.name AS genre_name
            FROM films f
            LEFT JOIN film_genre fg ON f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            JOIN mpa_rating m ON f.mpa_id = m.mpa_id
            WHERE f.film_id = ?
            """;
    private static final String GET_ALL_FILMS_SQL = """
            SELECT f.*, m.name AS mpa_name, g.genre_id, g.name AS genre_name
            FROM films f
            LEFT JOIN film_genre fg ON f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            LEFT JOIN mpa_rating m ON f.mpa_id = m.mpa_id
            ORDER BY f.film_id
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
        Map<Integer, Film> filmMap = new HashMap<>();
        FilmRowMapper rowMapper = new FilmRowMapper(filmMap);
        jdbcTemplate.query(FIND_FILM_WITH_GENRES_SQL, rowMapper, id);
        return Optional.ofNullable(filmMap.get(id));
    }


    public List<Film> getAll() {
        Map<Integer, Film> filmMap = new HashMap<>();
        FilmRowMapper rowMapper = new FilmRowMapper(filmMap);
        jdbcTemplate.query(GET_ALL_FILMS_SQL, rowMapper);
        return new ArrayList<>(filmMap.values());
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
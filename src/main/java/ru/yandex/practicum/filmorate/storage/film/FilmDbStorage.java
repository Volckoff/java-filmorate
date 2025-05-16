package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper rowMapper;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreRowMapper genreRowMapper;

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
    private static final String ADD_LIKES_SQL = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
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
    private static final String INSERT_IN_FILM_GENRE = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";


    @Override
    public Film createFilm(Film film) {
        Integer generatedId = insert(INSERT_FILM_SQL,
                true,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        film.setId(generatedId);
        updateMpaName(film);
        intoTable(film);
        film.setGenres(genreDbStorage.getGenresForFilm(film));
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
        return film;
    }


    private void updateMpaName(Film film) {
        Integer mpaId = film.getMpa().getId();
        String mpaName = mpaDbStorage.getNameForMpaId(mpaId);
        film.getMpa().setName(mpaName);
    }


    @Override
    public Optional<Film> getById(int id) {
        try {
            Film film = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, new Object[]{id}, rowMapper);
            if (film == null) {
                throw new NotFoundException("Фильм не найден");
            }
            film.setLikes(getLikesForFilm(id));
            film.setGenres(genreDbStorage.getGenresForFilm(film));
            updateMpaName(film);
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }


    @Override
    public List<Film> getAll() {
        List<Film> films = jdbcTemplate.query(FIND_ALL_FILMS_SQL, rowMapper);
        for (Film film : films) {
            film.setLikes(getLikesForFilm(film.getId()));
            film.setGenres(genreDbStorage.getGenresForFilm(film));
            updateMpaName(film);
        }
        return films;
    }


    public Set<Integer> getLikesForFilm(Integer filmId) {
        return new HashSet<>(jdbcTemplate.queryForList(GET_LIKES_FOR_FILM_SQL, Integer.class, filmId));
    }


    public void addLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(ADD_LIKES_SQL, filmId, userId);
    }


    public void removeLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(REMOVE_LIKE_SQL, filmId, userId);
    }


    public List<Film> getPopular(int count) {
        return jdbcTemplate.query(GET_POPULAR_FILMS_SQL, rowMapper, count);
    }


    private void intoTable(Film film) {
        Set<Genre> genres = film.getGenres();
        Set<Integer> genreId;
        for (Genre genre : genres) {
            insert(INSERT_IN_FILM_GENRE,
                    false,
                    film.getId(),
                    genre.getId()
            );
        }
    }


    private Integer insert(String query, boolean expectGeneratedKey, Object... params) {
        if (expectGeneratedKey) {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                for (int idx = 0; idx < params.length; idx++) {
                    ps.setObject(idx + 1, params[idx]);
                }
                return ps;
            }, keyHolder);
            return keyHolder.getKey().intValue();
        } else {
            int rowsCreated = jdbcTemplate.update(query, params);
            if (rowsCreated == 0) {
                throw new InternalServerException("Не удалось обновить данные");
            }
            return rowsCreated;
        }
    }
}
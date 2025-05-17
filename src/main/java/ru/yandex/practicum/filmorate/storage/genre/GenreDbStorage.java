package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Repository
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_ALL_GENRES = "SELECT * FROM genres ORDER BY genre_id";
    private static final String FIND_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String FIND_GENRES_FOR_FILM_SQL = """
            SELECT g.genre_id, g.name FROM genres g
            JOIN film_genre fg ON g.genre_id = fg.genre_id
            WHERE fg.film_id = ?
            ORDER BY g.genre_id
            """;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Set<Genre> getGenresForFilm(Integer filmId) {
        return new TreeSet<>(jdbcTemplate.query(FIND_GENRES_FOR_FILM_SQL, new GenreRowMapper(), filmId));
    }


    public Genre getGenreById(int id) {
        try {
            return jdbcTemplate.queryForObject(FIND_GENRE_BY_ID,
                    (rs, rn) -> new Genre(rs.getInt("genre_id"),
                            rs.getString("name")), id);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Жанр с ID " + id + " не найден");
        }
    }


    public List<Genre> getAll() {
        return jdbcTemplate.query(FIND_ALL_GENRES, new GenreRowMapper());
    }

    public boolean existsById(Integer genreId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM genres WHERE genre_id = ?", Integer.class, genreId);
        return count != null && count > 0;
    }
}

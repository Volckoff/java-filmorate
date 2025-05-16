package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
public class GenreDbStorage {
    protected final JdbcTemplate jdbc;
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY genre_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String FIND_NAME_BY_ID_QUERY = "SELECT name FROM genres WHERE genre_id = ?";
    private static final String FIND_ALL_BY_FILM_ID_QUERY = """
            SELECT fg.genre_id FROM film_genre AS fg
            WHERE fg.film_id = ?
            """;
    private static final String FIND_ALL_GENRE_BE_FILM_ID =
            "SELECT g.genre_id, g.name FROM genres g JOIN film_genre fg ON g.genre_id = fg.genre_id " +
                    "WHERE fg.film_id = ? ORDER BY genre_id";


    public GenreDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Genre getGenreById(int id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_QUERY, new Object[]{id}, new GenreRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Genre c таким id не найден: " + id);
        }
    }


    public Collection<Genre> getAllGenre() {
        return jdbc.query(FIND_ALL_QUERY, new GenreRowMapper());
    }

    public String getNameForGenreId(Integer id) {
        return jdbc.queryForObject(FIND_NAME_BY_ID_QUERY, new Object[]{id}, String.class);
    }

    public Set<Genre> findGenreByFilmId(Integer filmId) {
        List<Integer> genresId = jdbc.queryForList(FIND_ALL_BY_FILM_ID_QUERY, new Object[]{filmId}, Integer.class);
        Set<Genre> genres = new HashSet<>();
        for (Integer id : genresId) {
            Genre genre = new Genre();
            genre.setId(id);
            genre.setName(getNameForGenreId(id));
            genres.add(genre);
        }
        return genres;
    }

    public Set<Genre> getGenresForFilm(Film film) {
        List<Genre> genres = jdbc.query(FIND_ALL_GENRE_BE_FILM_ID, new GenreRowMapper(), film.getId());
        return new TreeSet<>(genres);
    }
}

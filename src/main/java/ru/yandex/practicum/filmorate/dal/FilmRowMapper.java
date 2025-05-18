package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;


@Component
public class FilmRowMapper implements RowMapper<Film> {

    private Map<Integer, Film> filmMap;

    public FilmRowMapper(Map<Integer, Film> filmMap) {
        this.filmMap = filmMap;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer filmId = rs.getObject("film_id", Integer.class);
        Film film = filmMap.get(filmId);

        if (film == null) {
            film = new Film();
            film.setId(filmId);
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getObject("release_date", LocalDate.class));
            film.setDuration(rs.getObject("duration", Integer.class));
            Mpa mpa = new Mpa();
            mpa.setId(rs.getObject("mpa_id", Integer.class));
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);
            film.setGenres(new LinkedHashSet<>());
            film.setLikes(new HashSet<>());
            filmMap.put(filmId, film);
        }
        Integer genreId = rs.getObject("genre_id", Integer.class);
        if (genreId != null) {
            Genre genre = new Genre();
            genre.setId(genreId);
            genre.setName(rs.getString("genre_name"));
            film.getGenres().add(genre);
        }
        return film;
    }
}
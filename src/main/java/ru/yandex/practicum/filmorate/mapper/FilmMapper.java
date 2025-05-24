package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.Set;

@Component
public class FilmMapper {

    private static final int GENRE_MIN = 1;
    private static final int GENRE_MAX = 6;
    private static final int MPA_MIN = 1;
    private static final int MPA_MAX = 6;

    public Film toFilm(NewFilmRequest request) {
        if (request.getMpa().getId() > MPA_MAX || request.getMpa().getId() < MPA_MIN) {
            throw new NotFoundException("Не верный id Mpa");
        }
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpa(request.getMpa());
        if (request.getGenres() != null) {
            Set<Genre> genres = request.getGenres();
            for (Genre genre : genres) {
                if (genre.getId() > GENRE_MAX || genre.getId() < GENRE_MIN) {
                    throw new NotFoundException("Не верный id Genre");
                }
            }
            film.setGenres(new HashSet<>(request.getGenres()));
        }
        return film;
    }


    public Film updateFromRequest(Film existing, UpdateFilmRequest request) {
        if (request.hasName()) existing.setName(request.getName());
        if (request.hasDescription()) existing.setDescription(request.getDescription());
        if (request.hasReleaseDate()) existing.setReleaseDate(request.getReleaseDate());
        if (request.hasDuration()) existing.setDuration(request.getDuration());
        if (request.hasMpa()) existing.setMpa(request.getMpa());
        if (request.hasGenres()) existing.setGenres(new HashSet<>(request.getGenres()));
        return existing;
    }


    public FilmDto toDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpa(film.getMpa());
        dto.setGenres(film.getGenres());
        dto.setLikes(film.getLikes());
        return dto;
    }
}

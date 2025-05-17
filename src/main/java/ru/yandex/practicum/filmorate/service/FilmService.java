package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmDbStorage filmDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final FilmMapper filmMapper;

    public FilmDto createFilm(NewFilmRequest request) {
        validateMpa(request.getMpa().getId());
        Film film = filmMapper.toFilm(request);
        Film created = filmDbStorage.createFilm(film);
        if (request.hasGenres()) {
            Set<Integer> genreIds = request.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            validateGenres(genreIds);
            filmDbStorage.addGenres(created.getId(), genreIds);
        }
        Film fullFilm = filmDbStorage.getById(created.getId()).orElseThrow();
        fullFilm.setMpa(mpaDbStorage.getMpaById(fullFilm.getMpa().getId()));
        fullFilm.setGenres(genreDbStorage.getGenresForFilm(fullFilm.getId()));
        return filmMapper.toDto(fullFilm);
    }

    public Optional<FilmDto> getFilmById(int id) {
        log.info("Попытка получения фильма с id{}", id);
        return filmDbStorage.getById(id)
                .map(film -> {
                    film.setMpa(mpaDbStorage.getMpaById(film.getMpa().getId()));
                    film.setGenres(genreDbStorage.getGenresForFilm(film.getId()));
                    return filmMapper.toDto(film);
                });
    }


    public void addLike(Integer filmId, Integer userId) {
        log.info("Попытка добавления лайка от пользователя");
        filmDbStorage.addLike(filmId, userId);
        Film film = filmDbStorage.getById(filmId).orElseThrow();
        film.setLikes(filmDbStorage.getLikesForFilm(filmId));
    }


    public void removeLike(Integer filmId, Integer userId) {
        log.info("Попытка удаления лайка");
        filmDbStorage.removeLike(filmId, userId);
        Film film = filmDbStorage.getById(filmId).orElseThrow();
        film.setLikes(filmDbStorage.getLikesForFilm(filmId));
    }


    public List<FilmDto> getPopular(int count) {
        log.info("Получение списка популярных фильмов");
        List<Film> popularFilms = filmDbStorage.getPopular(count);
        for (Film film : popularFilms) {
            film.setMpa(mpaDbStorage.getMpaById(film.getMpa().getId()));
            film.setGenres(genreDbStorage.getGenresForFilm(film.getId()));
        }
        return popularFilms.stream()
                .map(filmMapper::toDto)
                .toList();
    }


    public List<FilmDto> getAllFilms() {
        log.info("Получение списка всех фильмов");
        List<Film> films = filmDbStorage.getAll();
        for (Film film : films) {
            film.setMpa(mpaDbStorage.getMpaById(film.getMpa().getId()));
            film.setGenres(genreDbStorage.getGenresForFilm(film.getId()));
        }
        return films.stream()
                .map(filmMapper::toDto)
                .toList();
    }


    public FilmDto updateFilm(UpdateFilmRequest request) {
        Film existing = filmDbStorage.getById(request.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        Film updated = filmMapper.updateFromRequest(existing, request);
        if (request.hasMpa()) {
            validateMpa(updated.getMpa().getId());
        }
        filmDbStorage.updateFilm(updated);
        if (request.hasGenres()) {
            Set<Integer> genreIds = request.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            validateGenres(genreIds);
        }
        Film fullFilm = filmDbStorage.getById(updated.getId()).orElseThrow();
        fullFilm.setMpa(mpaDbStorage.getMpaById(fullFilm.getMpa().getId()));
        fullFilm.setGenres(genreDbStorage.getGenresForFilm(fullFilm.getId()));
        return filmMapper.toDto(fullFilm);
    }


    private void validateMpa(Integer mpaId) {
        if (!mpaDbStorage.existsById(mpaId)) {
            throw new NotFoundException("MPA с ID " + mpaId + " не найден");
        }
    }


    private void validateGenres(Set<Integer> genreIds) {
        for (Integer genreId : genreIds) {
            if (!genreDbStorage.existsById(genreId)) {
                throw new NotFoundException("Жанр с ID " + genreId + " не найден");
            }
        }
    }
}
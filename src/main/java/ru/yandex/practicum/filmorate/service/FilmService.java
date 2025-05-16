package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class FilmService {

    private final FilmDbStorage filmDbStorage; // Для специфичных методов
    private final FilmMapper filmMapper;
    private final UserDbStorage userDbStorage;


    public FilmService(
            @Qualifier("filmDbStorage") FilmDbStorage filmDbStorage,
            FilmMapper filmMapper, UserDbStorage userDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.filmMapper = filmMapper;
        this.userDbStorage = userDbStorage;
    }


    public FilmDto createFilm(NewFilmRequest request) {
        log.info("Попытка создания нового фильма");
        Film film = filmMapper.mapToFilm(request);
        Film createdFilm = filmDbStorage.createFilm(film);
        log.info("Фильм успешно создан: {}", film);
        return filmMapper.mapToFilmDto(createdFilm);
    }


    public FilmDto updateFilm(UpdateFilmRequest request) {
        log.info("Попытка обновления фильма");
        Film existingFilm = filmDbStorage.getById(request.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        existingFilm = filmMapper.updateFromRequest(existingFilm, request);
        existingFilm = filmDbStorage.updateFilm(existingFilm);
        return filmMapper.mapToFilmDto(existingFilm);
    }


    public List<FilmDto> getAllFilms() {
        return filmDbStorage.getAll().stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
    }


    public Optional<FilmDto> getFilmById(Integer id) {
        return filmDbStorage.getById(id)
                .map(filmMapper::mapToFilmDto);
    }


    public void addLike(Integer filmId, Integer userId) {
        Optional<Film> optionalFilm = filmDbStorage.getById(filmId);
        Optional<User> optionalUser = userDbStorage.getById(userId);
        if (optionalFilm.isPresent()) {
            Film film = optionalFilm.get();
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                film.getLikes().add(user.getId());
            }
        }
        filmDbStorage.addLike(filmId, userId);
    }


    public void removeLike(Integer filmId, Integer userId) {
        filmDbStorage.removeLike(filmId, userId);
    }


    public List<FilmDto> getPopular(int count) {
        return filmDbStorage.getPopular(count).stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
    }
}
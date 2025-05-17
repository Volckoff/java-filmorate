package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.Update;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping
    public List<FilmDto> getAllFilms() {
        log.info("Запрос на получение всех фильмов");
        return filmService.getAllFilms();
    }

    @PostMapping
    @Validated(Create.class)
    public FilmDto createFilm(@RequestBody @Valid NewFilmRequest request) {
        log.info("Попытка создания фильма: {}", request);
        return filmService.createFilm(request);
    }

    @PutMapping
    @Validated(Update.class)
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequest request) {
        log.info("Попытка обновления фильма с ID {}: {}", request);
        return filmService.updateFilm(request);
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable @Positive int id) {
        log.info("Запрос на получение фильма с ID {}", id);
        return filmService.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
    }


    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        log.info("Добавление лайка фильму с ID {} от пользователя с ID {}", id, userId);
        filmService.addLike(id, userId);
    }


    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        log.info("Удаление лайка фильма с ID {} от пользователя с ID {}", id, userId);
        filmService.removeLike(id, userId);
    }


    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") @Positive int count) {
        log.info("Запрос на получение {} самых популярных фильмов", count);
        return filmService.getPopular(count);
    }
}

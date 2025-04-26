package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.Update;

import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Запрос на получение всех фильмов");
        return filmService.getAll();
    }

    @PostMapping
    public Film createFilm(@Validated(Create.class) @RequestBody Film film) {
        log.info("Попытка создания фильма: {}", film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Validated(Update.class) @RequestBody Film updatedFilm) {
        log.info("Попытка обновления фильма: {}", updatedFilm);
        return filmService.updateFilm(updatedFilm);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable @Positive int id) {
        log.info("Запрос на получение фильма с ID {}", id);
        return filmService.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден."));
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
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") @Positive int count) {
        log.info("Запрос на получение {} самых популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }
}

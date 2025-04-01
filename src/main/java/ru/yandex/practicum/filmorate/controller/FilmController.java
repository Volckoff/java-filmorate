package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();


    @GetMapping
    public Collection<Film> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return new ArrayList<>(films.values());
    }


    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Попытка создания фильма: {}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно создан: {}", film);
        return film;
    }

    private int getNextId() {
        return films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Попытка обновления фильма: {}", film);
        Integer id = film.getId();
        if (!films.containsKey(id)) {
            log.warn("Ошибка обновления фильма: фильм с ID {} не найден", id);
            throw new ValidationException("Фильм с таким ID не найден");
        }
        films.put(id, film);
        log.info("Фильм успешно обновлен: {}", film);
        return film;
    }

}

package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.Update;

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
    public Film createFilm(@Validated(Create.class) @RequestBody Film film) {
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
    public Film updateFilm(@Validated(Update.class) @RequestBody Film updatedFilm) {
        log.info("Попытка обновления фильма: {}", updatedFilm);
        Integer id = updatedFilm.getId();
        if (id == null || !films.containsKey(id)) {
            log.warn("Ошибка обновления фильма: фильм с ID {} не найден", id);
            throw new NotFoundException("Фильм с таким ID не найден");
        }

        Film existingFilm = films.get(id);
        if (updatedFilm.getName() != null) {
            existingFilm.setName(updatedFilm.getName());
        }
        if (updatedFilm.getDescription() != null) {
            existingFilm.setDescription(updatedFilm.getDescription());
        }
        if (updatedFilm.getReleaseDate() != null) {
            existingFilm.setReleaseDate(updatedFilm.getReleaseDate());
        }
        if (updatedFilm.getDuration() != null) {
            existingFilm.setDuration(updatedFilm.getDuration());
        }

        films.put(id, existingFilm);
        log.info("Фильм успешно обновлен: {}", existingFilm);
        return existingFilm;
    }

}

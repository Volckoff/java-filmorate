package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();


    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }


    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден.");
        }
        films.put(film.getId(), film);
        return film;
    }


    @Override
    public Optional<Film> getById(int id) {
        return Optional.ofNullable(films.get(id));
    }


    @Override
    public List<Film> getAll() {
        return List.copyOf(films.values());
    }


    private int getNextId() {
        return films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }
}

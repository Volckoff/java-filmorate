package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film createFilm(Film film); // Создание фильма

    Film updateFilm(Film film); // Обновление фильма

    Optional<Film> getById(int id); // Поиск фильма по ID

    List<Film> getAll(); // Получение всех фильмов
}

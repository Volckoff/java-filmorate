package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.DateBefore;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.Update;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    @NotNull(groups = Update.class)
    private Integer id;

    @NotBlank(message = "Название не может быть пустым", groups = Create.class)
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов", groups = {Create.class, Update.class})
    private String description;

    @DateBefore(message = "дата релиза — не раньше 28 декабря 1895 года", groups = {Create.class, Update.class})
    @NotNull(message = "Дата релиза обязательна", groups = Create.class)
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом", groups = {Create.class, Update.class})
    private Integer duration;

    private Set<Integer> likes;

    private Set<Genre> genres = new HashSet<>();

    @NotNull(message = "MPA рейтинг обязателен")
    private Mpa mpa;

    public Film() {
    }

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration,
                Set<Genre> genres, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
    }
}

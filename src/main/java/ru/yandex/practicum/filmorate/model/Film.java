package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.annotation.DateBefore;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.Update;

import java.time.LocalDate;

@Data
@Slf4j
public class Film {

    @NotNull(groups = Update.class, message = "ID не может быть null")
    private Integer id;

    @NotBlank(groups = Create.class, message = "Название фильма не может быть пустым")
    private String name;

    @Size(groups = {Update.class, Create.class}, max = 200, message = "Описание не может превышать 200 символов")
    private String description;

    @NotNull(groups = Create.class, message = "Дата релиза обязательна")
    @DateBefore(groups = {Update.class, Create.class}, message = "Дата релиза — не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @Positive(groups = {Update.class, Create.class}, message = "Продолжительность фильма должна быть " +
            "положительным числом")
    private Integer duration;

    public Film(int id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        log.info("Фильм успешно создан: {}", this);
    }
}

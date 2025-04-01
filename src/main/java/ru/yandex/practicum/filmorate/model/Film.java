package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

@Data
@Slf4j
public class Film {

    @NotNull(message = "ID не может быть null")
    private Integer id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        validate(id, name, description, releaseDate, duration);
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        log.info("Фильм успешно создан: {}", this);
    }

    private void validate(Integer id, String name, String description, LocalDate releaseDate, int duration) {
        if (id == null || id < 0) {
            log.error("Ошибка валидации: ID должен быть положительным числом");
            throw new ValidationException("ID должен быть положительным числом");
        }
        if (name == null || name.trim().isEmpty()) {
            log.error("Ошибка валидации: название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (description != null && description.length() > 200) {
            log.error("Ошибка валидации: описание не может превышать 200 символов");
            throw new ValidationException("Описание не может превышать 200 символов");
        }
        if (releaseDate == null || releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка валидации: дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (duration <= 0) {
            log.error("Ошибка валидации: продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}

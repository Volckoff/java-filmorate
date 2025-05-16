package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.DateBefore;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateFilmRequest {

    private Integer id;
    private String name;
    private String description;
    @DateBefore(message = "Дата релиза должна быть после 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;
    private Mpa mpa;
    private Set<Genre> genres;

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null && duration > 0;
    }

    public boolean hasMpaId() {
        return mpa != null;
    }

    public boolean hasGenres() {
        return genres != null && !genres.isEmpty();
    }
}

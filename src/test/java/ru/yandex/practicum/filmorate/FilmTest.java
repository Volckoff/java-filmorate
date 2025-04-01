package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmTest {

    @Test
    void testValidFilm() {
        assertDoesNotThrow(() -> new Film(1, "Name", "Description",
                        LocalDate.of(1972, 3, 14), 175),
                "Корректный фильм должен быть создан без ошибок");
    }

    @Test
    void testNameIsEmpty() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> new Film(1, "", "Description",
                        LocalDate.of(1972, 3, 14), 175));
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void testDescriptionTooLong() {
        String longDescription = "a".repeat(201);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> new Film(1, "Name", longDescription,
                        LocalDate.of(1972, 3, 14), 175));
        assertEquals("Описание не может превышать 200 символов", exception.getMessage());
    }

    @Test
    void testReleaseDateBefore1895() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> new Film(1, "Name", "Description",
                        LocalDate.of(1895, 12, 27), 175));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void testDurationIsNegative() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> new Film(1, "Name", "Description",
                        LocalDate.of(1972, 3, 14), -1));
        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }
}

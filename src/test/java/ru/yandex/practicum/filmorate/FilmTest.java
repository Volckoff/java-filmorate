package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class FilmTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testValidFilm() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(1896, 12, 27), 20);
        Set<ConstraintViolation<Film>> violations = validator.validate(film, Create.class, Update.class);
        assertThat(violations).hasSize(0);
    }

    @Test
    void testNameIsEmpty() {
        Film film = new Film(1, "", "Description",
                LocalDate.of(1896, 12, 27), 20);
        Set<ConstraintViolation<Film>> violations = validator.validate(film, Create.class, Update.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Название фильма не может быть пустым");
    }

    @Test
    void testDescriptionTooLong() {
        String longString201 = "a".repeat(201);
        String longString200 = "a".repeat(200);
        String longString199 = "a".repeat(199);
        Film film = new Film(1, "Name", longString201,
                LocalDate.of(1896, 12, 27), 20);
        Film film2 = new Film(2, "Name", longString200,
                LocalDate.of(1896, 12, 27), 20);
        Film film3 = new Film(3, "Name", longString199,
                LocalDate.of(1896, 12, 27), 20);
        List<ConstraintViolation<Film>> violations = new ArrayList<>();
        violations.addAll(validator.validate(film, Create.class, Update.class));
        violations.addAll(validator.validate(film2, Create.class, Update.class));
        violations.addAll(validator.validate(film3, Create.class, Update.class));
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Описание не может превышать 200 символов");
    }

    @Test
    void testReleaseDateBefore() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(1895, 12, 27), 20);
        Film film2 = new Film(2, "Name", "Description",
                LocalDate.of(1895, 12, 28), 20);
        Film film3 = new Film(3, "Name", "Description",
                LocalDate.of(1895, 12, 29), 20);
        List<ConstraintViolation<Film>> violations = new ArrayList<>();
        violations.addAll(validator.validate(film, Create.class, Update.class));
        violations.addAll(validator.validate(film2, Create.class, Update.class));
        violations.addAll(validator.validate(film3, Create.class, Update.class));
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Дата релиза — не раньше 28 декабря 1895 года");
    }

    @Test
    void testDurationIsNegative() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(1896, 12, 27), -1);
        Film film2 = new Film(2, "Name", "Description",
                LocalDate.of(1896, 12, 27), 0);
        Film film3 = new Film(3, "Name", "Description",
                LocalDate.of(1896, 12, 27), 1);
        List<ConstraintViolation<Film>> violations = new ArrayList<>();
        violations.addAll(validator.validate(film, Create.class, Update.class));
        violations.addAll(validator.validate(film2, Create.class, Update.class));
        violations.addAll(validator.validate(film3, Create.class, Update.class));
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Продолжительность фильма должна быть положительным числом",
                        "Продолжительность фильма должна быть положительным числом");
    }

    @Test
    void releaseDateNullTest() {
        Film film = new Film(1, "Name", "Description", null, 20);
        Set<ConstraintViolation<Film>> violations = validator.validate(film, Create.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Дата релиза обязательна");
    }
}

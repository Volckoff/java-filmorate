package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testValidUser() {
        User user = new User(1, "email@mail.ru", "Login", "Name",
                LocalDate.now().minusYears(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user, Create.class, Update.class);
        assertThat(violations).hasSize(0);
    }

    @Test
    void testEmailIsEmpty() {
        User user = new User(1, "invalidmail.ru", "Login", "Name",
                LocalDate.now().minusYears(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user, Create.class, Update.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Электронная почта должна быть корректной");
    }

    @Test
    void testLoginContainsSpaces() {
        User user = new User(1, "email@mail.ru", "Log in", "Name",
                LocalDate.now().minusYears(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user, Create.class, Update.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Логин не должен содержать пробелы");
    }

    @Test
    void testBirthdayInFuture() {
        User user = new User(1, "email@mail.ru", "Login", "Name",
                LocalDate.now().plusDays(1));
        User user2 = new User(1, "email@mail.ru", "Login", "Name",
                LocalDate.now().minusDays(1));
        User user3 = new User(1, "email@mail.ru", "Login", "Name",
                LocalDate.now());
        List<ConstraintViolation<User>> violations = new ArrayList<>();
        violations.addAll(validator.validate(user, Create.class, Update.class));
        violations.addAll(validator.validate(user2, Create.class, Update.class));
        violations.addAll(validator.validate(user3, Create.class, Update.class));
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Дата рождения не может быть в будущем");
    }

    @Test
    void birthdayNullTest() {
        User user = new User(1, "email@mail.ru", "login", "name", null);
        Set<ConstraintViolation<User>> violations = validator.validate(user, Create.class);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Дата рождения обязательна");
    }

}

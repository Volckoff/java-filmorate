package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testValidUser() {
        assertDoesNotThrow(() -> new User(1, "user@example.com", "user_login", "User",
                        LocalDate.of(1990, 10, 25)),
                "Корректный пользователь должен быть создан без ошибок");
    }

    @Test
    void testEmailIsEmpty() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> new User(1, "", "user_login", "User",
                        LocalDate.of(1990, 10, 25)));
        assertEquals("Электронная почта должна содержать символ '@'", exception.getMessage());
    }

    @Test
    void testLoginContainsSpaces() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> new User(1, "user@example.com", "user login", "User",
                        LocalDate.of(1990, 10, 25)));
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getMessage());
    }

    @Test
    void testBirthdayInFuture() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> new User(1, "user@example.com", "user_login", "User",
                        LocalDate.of(3000, 1, 1)));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}

package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.Update;

import java.time.LocalDate;

@Data
@Slf4j
public class User {

    @NotNull(groups = Update.class, message = "ID не может быть null")
    private Integer id;

    @Email(groups = {Update.class, Create.class}, message = "Электронная почта должна быть корректной")
    @NotBlank(groups = Create.class, message = "Электронная почта не может быть пустой")
    private String email;

    @NotBlank(groups = Create.class, message = "Логин не может быть пустым")
    @Pattern(groups = {Update.class, Create.class}, regexp = "\\S+", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(groups = {Update.class, Create.class}, message = "Дата рождения не может быть в будущем")
    @NotNull(groups = Create.class, message = "Дата рождения обязательна")
    private LocalDate birthday;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        validate(id, email, login, name, birthday);
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = (name == null || name.trim().isEmpty()) ? login : name;
        this.birthday = birthday;
        log.info("Пользователь успешно создан: {}", this);
    }

    private void validate(Integer id, String email, String login, String name, LocalDate birthday) {
        if (id == null || id < 0) {
            log.error("Ошибка валидации: ID должен быть положительным числом");
            throw new ValidationException("ID должен быть положительным числом");
        }
        if (birthday == null || birthday.isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (email == null || !email.contains("@")) {
            log.error("Ошибка валидации: электронная почта должна содержать символ '@'");
            throw new ValidationException("Электронная почта должна содержать символ '@'");
        }
        if (login == null || login.trim().isEmpty() || login.contains(" ")) {
            log.error("Ошибка валидации: логин не может быть пустым или содержать пробелы");
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
    }
}

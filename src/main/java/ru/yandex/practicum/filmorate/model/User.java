package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.Update;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    private Set<Integer> friends = new HashSet<>();

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = (name == null || name.trim().isEmpty()) ? login : name;
        this.birthday = birthday;
        log.info("Пользователь успешно создан: {}", this);
    }

    public User() {
    }

}

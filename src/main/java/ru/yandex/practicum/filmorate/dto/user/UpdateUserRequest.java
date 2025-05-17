package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.Create;
import ru.yandex.practicum.filmorate.validation.Update;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class UpdateUserRequest {

    @NotNull(message = "ID не может быть null")
    private Integer id;
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    private String login;
    @Email(message = "Электронная почта должна быть корректной")
    private String email;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public boolean hasLogin() {
        return !(login == null || login.isBlank());
    }


    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }


    public boolean hasName() {
        return !(name == null || name.isBlank());
    }


    public boolean hasBirthday() {
        return birthday != null;
    }
}
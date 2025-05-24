package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class NewUserRequest {
    Integer id;
    @Email
    @NotNull
    @NotBlank
    String email;
    @NotNull
    @NotBlank
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелов")
    String login;
    String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @NotNull
    LocalDate birthday;
}

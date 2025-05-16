package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class UpdateUserRequest {

    private Integer id;
    private String login;
    @Email
    private String email;
    private String name;
    @PastOrPresent
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
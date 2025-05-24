package ru.yandex.practicum.filmorate.dto.genre;

import lombok.Data;

@Data
public class UpdateGenreRequest {
    private Integer id;
    private String name;

    public boolean hasName() {
        return name != null && !name.isBlank();
    }
}

package ru.yandex.practicum.filmorate.dto.genre;

import lombok.Data;

@Data
public class NewGenreRequest {
    private Integer id;
    private String name;
}

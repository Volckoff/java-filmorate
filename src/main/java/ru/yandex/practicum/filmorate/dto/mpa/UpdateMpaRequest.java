package ru.yandex.practicum.filmorate.dto.mpa;

import lombok.Data;

@Data
public class UpdateMpaRequest {

    private Integer id;
    private String name;

    public boolean hasName() {
        return name != null && !name.isBlank();
    }
}

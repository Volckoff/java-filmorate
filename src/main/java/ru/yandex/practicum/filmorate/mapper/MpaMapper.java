package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

@Component
public class MpaMapper {

    public MpaDto mapToMpaDto(Mpa mpa) {
        MpaDto dto = new MpaDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getName());
        return dto;
    }
}
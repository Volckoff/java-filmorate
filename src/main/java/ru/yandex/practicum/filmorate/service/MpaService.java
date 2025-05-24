package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MpaService {
    private final MpaDbStorage mpaDbStorage;
    private final MpaMapper mpaMapper;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage, MpaMapper mpaMapper) {
        this.mpaDbStorage = mpaDbStorage;
        this.mpaMapper = mpaMapper;
    }

    public MpaDto getMpaById(int id) {
        Mpa mpa = mpaDbStorage.getMpaById(id);
        return mpaMapper.mapToMpaDto(mpa);
    }

    public Collection<MpaDto> getAllMpa() {
        return mpaDbStorage.getAllMpa()
                .stream()
                .map(mpaMapper::mapToMpaDto)
                .collect(Collectors.toList());
    }
}

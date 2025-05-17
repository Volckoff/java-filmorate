package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GenreService {
    private final GenreDbStorage genreDbStorage;
    private final GenreMapper genreMapper;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage, GenreMapper genreMapper) {
        this.genreDbStorage = genreDbStorage;
        this.genreMapper = genreMapper;
    }

    public GenreDto getGenreById(int id) {
        Genre genre = genreDbStorage.getGenreById(id);
        return genreMapper.mapToGenreDto(genre);
    }

    public List<GenreDto> getAllGenre() {
        return genreDbStorage.getAll()
                .stream()
                .map(genreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }
}

package ru.yandex.practicum.filmorate.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    @Override
    public List<Genre> getAllGenres() {
        return genreStorage.findAll();
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        return genreStorage.findGenreById(id);
    }

    @Override
    public List<Genre> getFilmGenres(int filmId) {
        return genreStorage.getFilmGenres(filmId);
    }

    @Override
    public Map<Integer, List<Genre>> getGenresForFilms(List<Integer> filmIds) {
        return genreStorage.getGenresForFilms(filmIds);
    }

    @Override
    public void addGenresToFilm(int filmId, List<Genre> genres) {
        validateGenresExist(genres);
        genreStorage.addFilmGenres(filmId, genres);
        log.info("Added genres to film {}: {}", filmId, genres);
    }

    @Override
    public void updateFilmGenres(int filmId, List<Genre> genres) {
        validateGenresExist(genres);
        genreStorage.updateFilmGenres(filmId, genres);
        log.info("Updated genres for film {}: {}", filmId, genres);
    }

    private void validateGenresExist(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        for (Genre genre : genres) {
            if (genreStorage.findGenreById(genre.getId()).isEmpty()) {
                throw new NotFoundException("Genre with ID " + genre.getId() + " not found");
            }
        }
    }
}

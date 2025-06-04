package ru.yandex.practicum.filmorate.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    @Override
    public Director createDirector(Director director) {
        log.info("Creating director: {}", director);
        return directorStorage.createDirector(director);
    }

    @Override
    public List<Director> getDirectors() {
        log.info("Getting all directors");
        return directorStorage.getDirectors();
    }

    @Override
    public Director getDirector(Long id) {
        log.info("Getting director with id: {}", id);
        return directorStorage.getDirector(id)
                .orElseThrow(() -> new NotFoundException("Director with id " + id + " not found"));
    }

    @Override
    public Director updateDirector(Director director) {
        log.info("Updating director: {}", director);
        return directorStorage.updateDirector(director)
                .orElseThrow(() -> new NotFoundException("Director with id " + director.getId() + " not found"));
    }

    @Override
    public void removeDirector(Long id) {
        log.info("Removing director with id: {}", id);
        directorStorage.removeDirector(id);
    }

    @Override
    public void addDirectorsToFilm(int filmId, List<Director> directors) {
        directorStorage.addDirectorsToFilm(filmId, directors);
    }

    @Override
    public void updateFilmDirectors(int filmId, List<Director> directors) {
        directorStorage.updateFilmDirectors(filmId, directors);
    }

    @Override
    public List<Director> getFilmDirectors(int filmId) {
        return directorStorage.getFilmDirectors(filmId);
    }

    @Override
    public Map<Integer, List<Director>> getDirectorsForFilms(List<Integer> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) {
            return new HashMap<>();
        }
        return directorStorage.getDirectorsForFilms(filmIds);
    }
}

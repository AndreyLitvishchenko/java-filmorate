package ru.yandex.practicum.filmorate.service;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorService {
    Director createDirector(Director director);

    List<Director> getDirectors();

    Director getDirector(Long id);

    Director updateDirector(Director director);

    void removeDirector(Long id);

    void addDirectorsToFilm(int filmId, List<Director> directors);

    void updateFilmDirectors(int filmId, List<Director> directors);

    List<Director> getFilmDirectors(int filmId);
}

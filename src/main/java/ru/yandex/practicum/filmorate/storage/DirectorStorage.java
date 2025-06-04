package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorStorage {
    Director createDirector(Director director);

    List<Director> getDirectors();

    Optional<Director> getDirector(Long id);

    Optional<Director> updateDirector(Director director);

    void removeDirector(Long id);

    void addDirectorsToFilm(int filmId, List<Director> directors);

    void updateFilmDirectors(int filmId, List<Director> directors);

    List<Director> getFilmDirectors(int filmId);

    Map<Integer, List<Director>> getDirectorsForFilms(List<Integer> filmIds);
}

package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreStorage {
    List<Genre> findAll();

    Optional<Genre> findGenreById(int id);

    List<Genre> getFilmGenres(int filmId);

    Map<Integer, List<Genre>> getGenresForFilms(List<Integer> filmIds);

    void addFilmGenres(int filmId, List<Genre> genres);

    void updateFilmGenres(int filmId, List<Genre> genres);
}

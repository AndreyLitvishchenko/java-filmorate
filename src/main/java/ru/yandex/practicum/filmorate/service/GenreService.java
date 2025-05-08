package ru.yandex.practicum.filmorate.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreService {

    List<Genre> getAllGenres();

    Optional<Genre> getGenreById(int id);

    List<Genre> getFilmGenres(int filmId);

    Map<Integer, List<Genre>> getGenresForFilms(List<Integer> filmIds);

    void addGenresToFilm(int filmId, List<Genre> genres);

    void updateFilmGenres(int filmId, List<Genre> genres);
}

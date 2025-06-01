package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Optional<Film> findFilmById(int id);

    List<Film> findAll();

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getMostPopularFilms(int count);

    List<Film> getFilmsByDirectorOrderBy(Long directorId, String sortBy);
}

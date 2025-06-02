package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Optional<Film> findFilmById(int id);

    List<Film> findAll();

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getMostPopularFilms(int count);

    List<Film> getFilmsByDirectorOrderBy(Long directorId, String sortBy);

    void removeFilm(int id);

    List<Integer> getLikedFilms(int userId);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> getMostPopularFilmsByGenre(int count, int genreId);

    List<Film> getMostPopularFilmsByYear(int count, int year);

    List<Film> getMostPopularFilmsByGenreAndYear(int count, int genreId, int year);
}

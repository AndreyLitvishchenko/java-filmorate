package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс сервиса для работы с фильмами
 */
public interface FilmService {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilmById(int id);

    List<Film> getAllFilms();

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    List<Film> getFilmsByDirectorOrderBy(Long directorId, String sortBy);

    void removeFilm(int id);

    List<Film> getCommonFilms(int userId, int friendId);
}

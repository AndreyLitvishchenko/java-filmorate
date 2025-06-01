package ru.yandex.practicum.filmorate.service;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Film;

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

    List<Film> getPopularFilms(int count);

    List<Film> getFilmsByDirectorOrderBy(Long directorId, String sortBy);
    
    void removeFilm(int id);

    List<Film> getCommonFilms(int userId, int friendId);
}

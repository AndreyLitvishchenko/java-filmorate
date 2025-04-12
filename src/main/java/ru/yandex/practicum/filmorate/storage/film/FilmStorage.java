package ru.yandex.practicum.filmorate.storage.film;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilmById(int id);

    void deleteFilm(int id);
}

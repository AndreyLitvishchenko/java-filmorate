package ru.yandex.practicum.filmorate.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Film with ID " + id + " not found"));
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public void addLike(int filmId, int userId) {
        Film film = getFilmById(filmId);

        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        film.getLikes().add(userId);
        filmStorage.updateFilm(film);

        log.info("User {} liked film {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilmById(filmId);

        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Like from user " + userId + " for film " + filmId + " not found");
        }

        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);

        log.info("User {} removed like from film {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Release date cannot be earlier than " + CINEMA_BIRTHDAY);
        }
    }
}

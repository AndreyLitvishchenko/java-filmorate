package ru.yandex.practicum.filmorate.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final UserService userService;
    private final DirectorService directorService;

    @Override
    public Film createFilm(Film film) {
        validateFilm(film);
        validateMpaExists(film.getMpa().getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            validateGenresExist(film.getGenres());
        }

        Film createdFilm = filmStorage.create(film);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreService.addGenresToFilm(createdFilm.getId(), film.getGenres());
            createdFilm.setGenres(genreService.getFilmGenres(createdFilm.getId()));
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            directorService.addDirectorsToFilm(createdFilm.getId(), film.getDirectors());
            createdFilm.setDirectors(directorService.getFilmDirectors(createdFilm.getId()));
        }

        log.info("Film created: {}", createdFilm);
        return createdFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilm(film);
        validateMpaExists(film.getMpa().getId());
        validateFilmExists(film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            validateGenresExist(film.getGenres());
        }

        Film updatedFilm = filmStorage.update(film);

        genreService.updateFilmGenres(film.getId(), film.getGenres());
        updatedFilm.setGenres(genreService.getFilmGenres(film.getId()));

        directorService.updateFilmDirectors(film.getId(), film.getDirectors());
        updatedFilm.setDirectors(directorService.getFilmDirectors(film.getId()));

        log.info("Film updated: {}", updatedFilm);
        return updatedFilm;
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        Optional<Film> filmOpt = filmStorage.findFilmById(id);

        if (filmOpt.isPresent()) {
            Film film = filmOpt.get();
            film.setGenres(genreService.getFilmGenres(id));
            film.setDirectors(directorService.getFilmDirectors(id));
            return Optional.of(film);
        }

        return Optional.empty();
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.findAll();

        if (!films.isEmpty()) {
            List<Integer> filmIds = films.stream()
                    .map(Film::getId)
                    .toList();

            Map<Integer, List<Genre>> filmGenres = genreService.getGenresForFilms(filmIds);

            films.forEach(film -> {
                film.setGenres(filmGenres.getOrDefault(film.getId(), new ArrayList<>()));
                film.setDirectors(directorService.getFilmDirectors(film.getId()));
            });
        }

        return films;
    }

    @Override
    public void addLike(int filmId, int userId) {
        validateFilmExists(filmId);
        validateUserExists(userId);

        filmStorage.addLike(filmId, userId);
        log.info("User {} liked film {}", userId, filmId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        validateFilmExists(filmId);
        validateUserExists(userId);

        filmStorage.removeLike(filmId, userId);
        log.info("User {} removed like from film {}", userId, filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> films = filmStorage.getMostPopularFilms(count);

        if (!films.isEmpty()) {
            List<Integer> filmIds = films.stream()
                    .map(Film::getId)
                    .toList();

            Map<Integer, List<Genre>> filmGenres = genreService.getGenresForFilms(filmIds);

            films.forEach(film -> {
                film.setGenres(filmGenres.getOrDefault(film.getId(), new ArrayList<>()));
                film.setDirectors(directorService.getFilmDirectors(film.getId()));
            });
        }

        return films;
    }

    @Override
    public List<Film> getFilmsByDirectorOrderBy(Long directorId, String sortBy) {
        List<Film> films = filmStorage.getFilmsByDirectorOrderBy(directorId, sortBy);

        if (!films.isEmpty()) {
            List<Integer> filmIds = films.stream()
                    .map(Film::getId)
                    .toList();

            Map<Integer, List<Genre>> filmGenres = genreService.getGenresForFilms(filmIds);

            films.forEach(film -> {
                film.setGenres(filmGenres.getOrDefault(film.getId(), new ArrayList<>()));
                film.setDirectors(directorService.getFilmDirectors(film.getId()));
            });
        }

        return films;
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Release date cannot be earlier than " + CINEMA_BIRTHDAY);
        }
    }

    private void validateFilmExists(int filmId) {
        if (filmStorage.findFilmById(filmId).isEmpty()) {
            throw new NotFoundException("Film with ID " + filmId + " not found");
        }
    }

    private void validateUserExists(int userId) {
        if (userService.getUserById(userId).isEmpty()) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
    }

    private void validateMpaExists(int mpaId) {
        if (mpaService.getMpaById(mpaId).isEmpty()) {
            throw new NotFoundException("MPA with ID " + mpaId + " not found");
        }
    }

    private void validateGenresExist(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        List<Integer> genreIds = genres.stream()
                .map(Genre::getId)
                .distinct()
                .toList();

        for (Integer genreId : genreIds) {
            if (genreService.getGenreById(genreId).isEmpty()) {
                throw new NotFoundException("Genre with ID " + genreId + " not found");
            }
        }
    }
}

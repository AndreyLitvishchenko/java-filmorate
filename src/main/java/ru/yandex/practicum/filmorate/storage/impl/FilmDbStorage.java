package ru.yandex.practicum.filmorate.storage.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Override
    public Film create(Film film) {
        validateMpaExists(film.getMpa().getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                validateGenreExists(genre.getId());
            }
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)",
                    new String[] { "film_id" });
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreStorage.addFilmGenres(film.getId(), film.getGenres());
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
        }

        log.info("Film created: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        validateMpaExists(film.getMpa().getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                validateGenreExists(genre.getId());
            }
        }

        int rowsUpdated = jdbcTemplate.update(
                "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (rowsUpdated == 0) {
            throw new NotFoundException("Film with ID " + film.getId() + " not found");
        }

        genreStorage.updateFilmGenres(film.getId(), film.getGenres());
        film.setGenres(genreStorage.getFilmGenres(film.getId()));

        log.info("Film updated: {}", film);
        return film;
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        List<Film> films = jdbcTemplate.query(
                "SELECT f.*, m.name as mpa_name FROM films f " +
                        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "WHERE f.film_id = ?",
                this::mapRowToFilm,
                id);

        if (films.isEmpty()) {
            return Optional.empty();
        }

        Film film = films.get(0);
        film.setGenres(genreStorage.getFilmGenres(id));
        return Optional.of(film);
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query(
                "SELECT f.*, m.name as mpa_name FROM films f " +
                        "JOIN mpa m ON f.mpa_id = m.mpa_id",
                this::mapRowToFilm);

        for (Film film : films) {
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
        }

        return films;
    }

    @Override
    public void addLike(int filmId, int userId) {
        validFilmExists(filmId);
        jdbcTemplate.update(
                "INSERT INTO likes (film_id, user_id) VALUES (?, ?)",
                filmId, userId);
        log.info("User {} liked film {}", userId, filmId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        validFilmExists(filmId);
        jdbcTemplate.update(
                "DELETE FROM likes WHERE film_id = ? AND user_id = ?",
                filmId, userId);
        log.info("User {} removed like from film {}", userId, filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        List<Film> films = jdbcTemplate.query(
                "SELECT f.*, m.name as mpa_name, COUNT(l.user_id) as likes_count " +
                        "FROM films f " +
                        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "LEFT JOIN likes l ON f.film_id = l.film_id " +
                        "GROUP BY f.film_id " +
                        "ORDER BY likes_count DESC " +
                        "LIMIT ?",
                this::mapRowToFilm,
                count);

        for (Film film : films) {
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
        }

        return films;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
        return film;
    }

    private void validFilmExists(int filmId) {
        if (!jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?)",
                Boolean.class,
                filmId)) {
            throw new NotFoundException("Film with ID " + filmId + " not found");
        }
    }

    private void validateMpaExists(int mpaId) {
        mpaStorage.findMpaById(mpaId)
                .orElseThrow(() -> new NotFoundException("MPA with ID " + mpaId + " not found"));
    }

    private void validateGenreExists(int genreId) {
        genreStorage.findGenreById(genreId)
                .orElseThrow(() -> new NotFoundException("Genre with ID " + genreId + " not found"));
    }
}

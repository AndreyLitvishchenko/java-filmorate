package ru.yandex.practicum.filmorate.storage.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    @Override
    public Film create(Film film) {
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

        log.info("Film created with ID: {}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
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

        log.info("Film updated with ID: {}", film.getId());
        return film;
    }

    @Override
    public Optional<Film> findFilmById(int id) {
                        
        List<Film> films = jdbcTemplate.query(
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, "
                        + "f.mpa_id, m.name as mpa_name FROM films f "
                        + "JOIN mpa m ON f.mpa_id = m.mpa_id "
                        + "WHERE f.film_id = ?",
                filmMapper,
                id);

        return films.isEmpty() ? Optional.empty() : Optional.of(films.get(0));
    }

    @Override
    public List<Film> findAll() {
                        
        return jdbcTemplate.query(
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, "
                        + "f.mpa_id, m.name as mpa_name FROM films f " +
                        "JOIN mpa m ON f.mpa_id = m.mpa_id",
                filmMapper);
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbcTemplate.update(
                "INSERT INTO likes (film_id, user_id) VALUES (?, ?)",
                filmId, userId);
        log.info("Added like from user {} to film {}", userId, filmId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update(
                "DELETE FROM likes WHERE film_id = ? AND user_id = ?",
                filmId, userId);
        log.info("Removed like from user {} to film {}", userId, filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
                        
        return jdbcTemplate.query(
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                        "m.name as mpa_name, COUNT(l.user_id) as likes_count " +
                        "FROM films f " +
                        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "LEFT JOIN likes l ON f.film_id = l.film_id " +
                        "GROUP BY f.film_id " +
                        "ORDER BY likes_count DESC " +
                        "LIMIT ?",
                filmMapper, count);
    }
}

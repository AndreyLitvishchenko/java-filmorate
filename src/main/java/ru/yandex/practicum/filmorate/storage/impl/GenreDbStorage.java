package ru.yandex.practicum.filmorate.storage.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> genreRowMapper = (rs, rowNum) -> new Genre(rs.getInt("genre_id"),
            rs.getString("name"));

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT genre_id, name FROM genres ORDER BY genre_id", genreRowMapper);
    }

    @Override
    public Optional<Genre> findGenreById(int id) {
        List<Genre> genres = jdbcTemplate.query(
                "SELECT genre_id, name FROM genres WHERE genre_id = ?",
                genreRowMapper,
                id);
        return genres.isEmpty() ? Optional.empty() : Optional.of(genres.get(0));
    }

    @Override
    public List<Genre> getFilmGenres(int filmId) {
        return jdbcTemplate.query(
                "SELECT g.genre_id, g.name FROM genres g " +
                        "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                        "WHERE fg.film_id = ? " +
                        "ORDER BY g.genre_id",
                genreRowMapper,
                filmId);
    }

    @Override
    public void addFilmGenres(int filmId, List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        Set<Integer> uniqueGenres = new HashSet<>();
        for (Genre genre : genres) {
            if (uniqueGenres.add(genre.getId())) {
                jdbcTemplate.update(
                        "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                        filmId, genre.getId());
            }
        }
        log.info("Added genres to film {}: {}", filmId, genres);
    }

    @Override
    public void updateFilmGenres(int filmId, List<Genre> genres) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);

        if (genres == null || genres.isEmpty()) {
            return;
        }

        addFilmGenres(filmId, new ArrayList<>(genres));
        log.info("Updated genres for film {}: {}", filmId, genres);
    }
}

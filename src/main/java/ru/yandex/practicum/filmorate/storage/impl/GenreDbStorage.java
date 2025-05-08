package ru.yandex.practicum.filmorate.storage.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.mapper.FilmGenreMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

@Repository
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper genreMapper;
    private final FilmGenreMapper filmGenreMapper;

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query(
                "SELECT genre_id, name FROM genres ORDER BY genre_id", genreMapper);
    }

    @Override
    public Optional<Genre> findGenreById(int id) {
        List<Genre> genres = jdbcTemplate.query(
                "SELECT genre_id, name FROM genres WHERE genre_id = ?", genreMapper, id);
        return genres.isEmpty() ? Optional.empty() : Optional.of(genres.get(0));
    }

    @Override
    public List<Genre> getFilmGenres(int filmId) {
        return jdbcTemplate.query(
                "SELECT g.genre_id, g.name FROM genres g "
                        + "JOIN film_genres fg ON g.genre_id = fg.genre_id "
                        + "WHERE fg.film_id = ? "
                        + "ORDER BY g.genre_id", genreMapper, filmId);
    }

    @Override
    public Map<Integer, List<Genre>> getGenresForFilms(List<Integer> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) {
            return new java.util.HashMap<>();
        }

        String inSql = String.join(",", java.util.Collections.nCopies(filmIds.size(), "?"));

        List<Map.Entry<Integer, Genre>> filmGenres = jdbcTemplate.query(
                String.format("SELECT fg.film_id, g.genre_id, g.name FROM genres g " +
                        "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                        "WHERE fg.film_id IN (%s) " +
                        "ORDER BY fg.film_id, g.genre_id", inSql),
                filmGenreMapper,
                filmIds.toArray());

        Map<Integer, List<Genre>> result = new java.util.HashMap<>();
        for (Map.Entry<Integer, Genre> entry : filmGenres) {
            result.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
        }

        return result;
    }

    @Override
    public void addFilmGenres(int filmId, List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        Set<Integer> uniqueGenres = new HashSet<>();
        for (Genre genre : genres) {
            uniqueGenres.add(genre.getId());
        }

        List<Object[]> batchArgs = new ArrayList<>();
        for (Integer genreId : uniqueGenres) {
            batchArgs.add(new Object[] { filmId, genreId });
        }

        jdbcTemplate.batchUpdate(
                "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                batchArgs);

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

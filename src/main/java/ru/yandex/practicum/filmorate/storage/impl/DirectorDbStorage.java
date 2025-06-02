package ru.yandex.practicum.filmorate.storage.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");

        Map<String, Object> params = Map.of(
                "director_name", director.getName());

        long directorId = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        director.setId(directorId);
        log.info("Director created with id: {}", directorId);

        return director;
    }

    @Override
    public List<Director> getDirectors() {
        String sql = "SELECT director_id, director_name FROM directors";
        return jdbcTemplate.query(sql, new DirectorMapper());
    }

    @Override
    public Optional<Director> getDirector(Long id) {
        String sql = "SELECT director_id, director_name FROM directors WHERE director_id = ?";
        try {
            Director director = jdbcTemplate.queryForObject(sql, new DirectorMapper(), id);
            return Optional.ofNullable(director);
        } catch (Exception e) {
            log.error("Director with id {} not found", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Director> updateDirector(Director director) {
        String sql = "UPDATE directors SET director_name = ? WHERE director_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, director.getName(), director.getId());

        if (rowsAffected > 0) {
            log.info("Director updated with id: {}", director.getId());
            return Optional.of(director);
        } else {
            log.error("Director with id {} not found for update", director.getId());
            return Optional.empty();
        }
    }

    @Override
    public void removeDirector(Long id) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        if (rowsAffected > 0) {
            log.info("Director deleted with id: {}", id);
        } else {
            log.warn("Director with id {} not found for deletion", id);
        }
    }

    @Override
    public void addDirectorsToFilm(int filmId, List<Director> directors) {


        jdbcTemplate.update("DELETE FROM directors_films WHERE film_id = ?", filmId);

        if (directors == null || directors.isEmpty()) {
            return;
        }

        for (Director director : directors) {
            jdbcTemplate.update("INSERT INTO directors_films (director_id, film_id) VALUES (?, ?)",
                    director.getId(), filmId);
        }

        log.info("Directors updated for film with id: {}", filmId);
    }

    @Override
    public void updateFilmDirectors(int filmId, List<Director> directors) {
        addDirectorsToFilm(filmId, directors);
    }

    @Override
    public List<Director> getFilmDirectors(int filmId) {
        String sql = "SELECT d.director_id, d.director_name FROM directors d " +
                "JOIN directors_films df ON d.director_id = df.director_id " +
                "WHERE df.film_id = ?";
        return jdbcTemplate.query(sql, new DirectorMapper(), filmId);
    }
}

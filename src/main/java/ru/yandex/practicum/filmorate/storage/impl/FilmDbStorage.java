package ru.yandex.practicum.filmorate.storage.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
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
                filmMapper, id);
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

    @Override
    public List<Film> getFilmsByDirectorOrderBy(Long directorId, String sortBy) {
        String sql;
        if ("year".equalsIgnoreCase(sortBy)) {
            sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                    "m.name as mpa_name " +
                    "FROM films f " +
                    "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "JOIN directors_films df ON f.film_id = df.film_id " +
                    "WHERE df.director_id = ? " +
                    "ORDER BY f.release_date";
        } else { // sortBy = "likes"
            sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                    "m.name as mpa_name, COUNT(l.user_id) as likes_count " +
                    "FROM films f " +
                    "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "JOIN directors_films df ON f.film_id = df.film_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "WHERE df.director_id = ? " +
                    "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name " +
                    "ORDER BY likes_count DESC";
        }

        return jdbcTemplate.query(sql, filmMapper, directorId);
    }

    @Override
    public void removeFilm(int id) {
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
        log.info("Removed film id={}", id);
    }

    @Override
    public List<Integer> getLikedFilms(int userId) {
        String sql = "SELECT film_id FROM likes WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, userId);
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name as mpa_name "
                +
                "FROM likes t1 " +
                "INNER JOIN likes t2 ON t1.film_id = t2.film_id " +
                "INNER JOIN films f ON t1.film_id = f.film_id " +
                "INNER JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "WHERE t1.user_id = ? AND t2.user_id = ? " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, m.name " +
                "ORDER BY COUNT(t1.user_id) DESC";
        return jdbcTemplate.query(sql, filmMapper, userId, friendId);
    }

    @Override
    public List<Film> getMostPopularFilmsByGenre(int count, int genreId) {
        return jdbcTemplate.query(
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                        "m.name as mpa_name, COUNT(l.user_id) as likes_count " +
                        "FROM films f " +
                        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "LEFT JOIN likes l ON f.film_id = l.film_id " +
                        "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                        "WHERE fg.genre_id = ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY likes_count DESC " +
                        "LIMIT ?",
                filmMapper, genreId, count);
    }

    @Override
    public List<Film> getMostPopularFilmsByYear(int count, int year) {
        return jdbcTemplate.query(
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                        "m.name as mpa_name, COUNT(l.user_id) as likes_count " +
                        "FROM films f " +
                        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "LEFT JOIN likes l ON f.film_id = l.film_id " +
                        "WHERE EXTRACT(YEAR FROM CAST(f.release_date AS date)) = ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY likes_count DESC " +
                        "LIMIT ?",
                filmMapper, year, count);
    }

    @Override
    public List<Film> getMostPopularFilmsByGenreAndYear(int count, int genreId, int year) {
        return jdbcTemplate.query(
                "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                        "m.name as mpa_name, COUNT(l.user_id) as likes_count " +
                        "FROM films f " +
                        "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "LEFT JOIN likes l ON f.film_id = l.film_id " +
                        "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                        "WHERE fg.genre_id = ? " +
                        "AND " +
                        "EXTRACT(YEAR FROM CAST(f.release_date AS date)) = ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY likes_count DESC " +
                        "LIMIT ?",
                filmMapper, genreId, year, count);
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        String searchParam = "%" + query.toLowerCase() + "%";
        String sql;

        if ("director,title".equals(by) || "title,director".equals(by)) {
            // Поиск по названию и режиссёру
            sql = "SELECT DISTINCT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                    "f.mpa_id, m.name as mpa_name, COUNT(DISTINCT l.user_id) as likes_count " +
                    "FROM films f " +
                    "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "WHERE f.film_id IN (" +
                    "   SELECT DISTINCT f2.film_id FROM films f2 WHERE LOWER(f2.name) LIKE ? " +
                    "   UNION " +
                    "   SELECT DISTINCT df.film_id FROM directors_films df " +
                    "   JOIN directors d ON df.director_id = d.director_id " +
                    "   WHERE LOWER(d.director_name) LIKE ?" +
                    ") " +
                    "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name " +
                    "ORDER BY likes_count DESC";
            return jdbcTemplate.query(sql, filmMapper, searchParam, searchParam);
        } else if ("director".equals(by)) {
            // Поиск только по режиссёру
            sql = "SELECT DISTINCT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                    "f.mpa_id, m.name as mpa_name, COUNT(DISTINCT l.user_id) as likes_count " +
                    "FROM films f " +
                    "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "WHERE f.film_id IN (" +
                    "   SELECT df.film_id FROM directors_films df " +
                    "   JOIN directors d ON df.director_id = d.director_id " +
                    "   WHERE LOWER(d.director_name) LIKE ?" +
                    ") " +
                    "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name " +
                    "ORDER BY likes_count DESC";
            return jdbcTemplate.query(sql, filmMapper, searchParam);
        } else {
            // Поиск только по названию (по умолчанию)
            sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                    "f.mpa_id, m.name as mpa_name, COUNT(l.user_id) as likes_count " +
                    "FROM films f " +
                    "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "WHERE LOWER(f.name) LIKE ? " +
                    "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name " +
                    "ORDER BY likes_count DESC";
            return jdbcTemplate.query(sql, filmMapper, searchParam);
        }
    }
}

package ru.yandex.practicum.filmorate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Genre;

@Component
public class FilmGenreMapper implements RowMapper<Map.Entry<Integer, Genre>> {

    @Override
    public Map.Entry<Integer, Genre> mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new java.util.AbstractMap.SimpleEntry<>(
                rs.getInt("film_id"),
                new Genre(rs.getInt("genre_id"), rs.getString("name"))
        );
    }
}

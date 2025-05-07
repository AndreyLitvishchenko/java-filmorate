package ru.yandex.practicum.filmorate.storage.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Mpa> mpaRowMapper = (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("name"));

    @Override
    public List<Mpa> findAll() {
        return jdbcTemplate.query("SELECT mpa_id, name FROM mpa ORDER BY mpa_id", mpaRowMapper);
    }

    @Override
    public Optional<Mpa> findMpaById(int id) {
        List<Mpa> mpas = jdbcTemplate.query(
                "SELECT mpa_id, name FROM mpa WHERE mpa_id = ?",
                mpaRowMapper,
                id);
        return mpas.isEmpty() ? Optional.empty() : Optional.of(mpas.get(0));
    }
}

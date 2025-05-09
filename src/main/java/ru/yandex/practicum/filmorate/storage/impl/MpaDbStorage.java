package ru.yandex.practicum.filmorate.storage.impl;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaMapper mpaMapper;

    @Override
    public List<Mpa> findAll() {
        return jdbcTemplate.query(
                "SELECT mpa_id, name FROM mpa ORDER BY mpa_id", mpaMapper);
    }

    @Override
    public Optional<Mpa> findMpaById(int id) {
        List<Mpa> mpas = jdbcTemplate.query(
                "SELECT mpa_id, name FROM mpa WHERE mpa_id = ?", mpaMapper, id);
        return mpas.isEmpty() ? Optional.empty() : Optional.of(mpas.get(0));
    }
}

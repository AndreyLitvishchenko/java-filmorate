package ru.yandex.practicum.filmorate.storage.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

@Repository
@RequiredArgsConstructor
public class EventStorageDb implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Instant timestamp, int userId, int entityId, String eventType, String eventOperation) {
        String sql = "INSERT INTO events (timestamp, user_id, event_type, event_operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, timestamp.toEpochMilli(), userId,
                eventType.toUpperCase(), eventOperation.toUpperCase(), entityId);
    }

    @Override
    public List<Event> get(int id) {
        String sql = "SELECT * FROM events WHERE user_id = ? ORDER BY timestamp ASC";
        return jdbcTemplate.query(sql, new EventMapper(), id);
    }
}

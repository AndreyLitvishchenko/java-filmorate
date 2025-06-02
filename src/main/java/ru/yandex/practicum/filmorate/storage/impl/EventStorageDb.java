package ru.yandex.practicum.filmorate.storage.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
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
        return jdbcTemplate.query(sql, this::makeEvent, id);
    }

    private Event makeEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .timestamp(rs.getLong("timestamp"))
                .userId(rs.getInt("user_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("event_operation"))
                .eventId(rs.getInt("event_id"))
                .entityId(rs.getInt("entity_id"))
                .build();
    }
}

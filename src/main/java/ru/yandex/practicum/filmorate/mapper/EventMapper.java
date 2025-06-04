package ru.yandex.practicum.filmorate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ru.yandex.practicum.filmorate.model.Event;

public class EventMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
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

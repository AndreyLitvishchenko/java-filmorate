package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.time.Instant;
import java.util.List;

public interface EventStorage {

    void add(Instant timestamp, int userId, int entityId, String eventType, String eventOperation);

    List<Event> get(int id);
}

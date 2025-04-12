package ru.yandex.practicum.filmorate.storage.film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @Override
    public List<Film> getAllFilms() {
        log.debug("Getting all films. Total count: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.debug("Film added: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Film with ID {} not found", film.getId());
            throw new NotFoundException("Film with ID " + film.getId() + " not found");
        }
        films.put(film.getId(), film);
        log.debug("Film updated: {}", film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        if (!films.containsKey(id)) {
            log.warn("Film with ID {} not found", id);
            return Optional.empty();
        }
        return Optional.of(films.get(id));
    }

    @Override
    public void deleteFilm(int id) {
        if (!films.containsKey(id)) {
            log.warn("Film with ID {} not found", id);
            throw new NotFoundException("Film with ID " + id + " not found");
        }
        films.remove(id);
        log.debug("Film with ID {} deleted", id);
    }
}

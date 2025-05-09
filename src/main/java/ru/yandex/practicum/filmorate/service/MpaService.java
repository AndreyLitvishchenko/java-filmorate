package ru.yandex.practicum.filmorate.service;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Mpa;

public interface MpaService {

    List<Mpa> getAllMpa();

    Optional<Mpa> getMpaById(int id);
}

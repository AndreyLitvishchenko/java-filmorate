package ru.yandex.practicum.filmorate.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    @Override
    public List<Mpa> getAllMpa() {
        return mpaStorage.findAll();
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        return mpaStorage.findMpaById(id);
    }
}

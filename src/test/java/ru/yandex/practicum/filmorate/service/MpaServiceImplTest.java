package ru.yandex.practicum.filmorate.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.impl.MpaServiceImpl;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

@ExtendWith(MockitoExtension.class)
class MpaServiceImplTest {

    @Mock
    private MpaStorage mpaStorage;

    @InjectMocks
    private MpaServiceImpl mpaService;

    private Mpa mpa;
    private List<Mpa> mpaList;

    @BeforeEach
    void setUp() {
        mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        mpaList = new ArrayList<>();
        mpaList.add(mpa);
    }

    @Test
    void shouldGetAllMpa() {
        when(mpaStorage.findAll()).thenReturn(mpaList);

        List<Mpa> result = mpaService.getAllMpa();

        assertEquals(1, result.size());
        assertEquals("G", result.get(0).getName());
    }

    @Test
    void shouldGetMpaById() {
        when(mpaStorage.findMpaById(1)).thenReturn(Optional.of(mpa));

        Optional<Mpa> result = mpaService.getMpaById(1);

        assertTrue(result.isPresent());
        assertEquals("G", result.get().getName());
    }

    @Test
    void shouldReturnEmptyOptionalWhenMpaNotFound() {
        when(mpaStorage.findMpaById(999)).thenReturn(Optional.empty());

        Optional<Mpa> result = mpaService.getMpaById(999);

        assertFalse(result.isPresent());
    }
}

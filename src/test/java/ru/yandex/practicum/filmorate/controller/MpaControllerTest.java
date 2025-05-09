package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

@ExtendWith(MockitoExtension.class)
class MpaControllerTest {
    @Mock
    private MpaService mpaService;

    @InjectMocks
    private MpaController mpaController;

    private Mpa mpa;
    private List<Mpa> mpaList;

    @BeforeEach
    void setUp() {
        mpa = new Mpa(1, "G");
        mpaList = List.of(
                new Mpa(1, "G"),
                new Mpa(2, "PG"),
                new Mpa(3, "PG-13"),
                new Mpa(4, "R"),
                new Mpa(5, "NC-17"));
    }

    @Test
    void shouldGetAllMpa() {
        when(mpaService.getAllMpa()).thenReturn(mpaList);

        List<Mpa> result = mpaController.getAllMpa();

        assertEquals(5, result.size());
        assertEquals("G", result.get(0).getName());
        assertEquals("PG", result.get(1).getName());
    }

    @Test
    void shouldGetMpaById() {
        when(mpaService.getMpaById(1)).thenReturn(Optional.of(mpa));

        Mpa result = mpaController.getMpaById(1);

        assertEquals(1, result.getId());
        assertEquals("G", result.getName());
    }

    @Test
    void shouldThrowExceptionWhenMpaNotFound() {
        when(mpaService.getMpaById(999)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> mpaController.getMpaById(999));
    }
}

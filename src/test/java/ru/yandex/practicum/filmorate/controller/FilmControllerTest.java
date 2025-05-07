package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {
    @Mock
    private FilmStorage filmStorage;

    @InjectMocks
    private FilmController filmController;

    private Film film;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));
    }

    @Test
    void shouldCreateValidFilm() {
        when(filmStorage.create(any(Film.class))).thenAnswer(invocation -> {
            Film f = invocation.getArgument(0);
            f.setId(1);
            return f;
        });

        Film createdFilm = filmController.createFilm(film);
        assertEquals(1, createdFilm.getId());
        assertEquals("Test Film", createdFilm.getName());
    }

    @Test
    void shouldNotCreateFilmWithReleaseDateBeforeCinemaBirthday() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // One day before cinema birthday
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldCreateFilmWithReleaseDateOnCinemaBirthday() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28)); // Cinema birthday

        when(filmStorage.create(any(Film.class))).thenAnswer(invocation -> {
            Film f = invocation.getArgument(0);
            f.setId(1);
            return f;
        });

        Film createdFilm = filmController.createFilm(film);
        assertEquals(1, createdFilm.getId());
    }

    @Test
    void shouldUpdateFilm() {
        film.setId(1);
        film.setName("Updated Film");

        when(filmStorage.update(any(Film.class))).thenReturn(film);

        Film updatedFilm = filmController.updateFilm(film);
        assertEquals("Updated Film", updatedFilm.getName());
    }

    @Test
    void shouldGetFilmById() {
        when(filmStorage.findFilmById(1)).thenReturn(Optional.of(film));
        Film foundFilm = filmController.getFilmById(1);
        assertEquals("Test Film", foundFilm.getName());
    }

    @Test
    void shouldThrowExceptionWhenFilmNotFound() {
        when(filmStorage.findFilmById(999)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> filmController.getFilmById(999));
    }
}

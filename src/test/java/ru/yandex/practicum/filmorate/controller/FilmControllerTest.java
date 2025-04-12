package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {
    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    @Test
    void shouldCreateValidFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film expectedFilm = new Film();
        expectedFilm.setId(1);
        expectedFilm.setName("Test Film");
        expectedFilm.setDescription("Test Description");
        expectedFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        expectedFilm.setDuration(120);

        when(filmService.addFilm(any(Film.class))).thenReturn(expectedFilm);

        Film createdFilm = filmController.createFilm(film);

        assertEquals(1, createdFilm.getId());
        assertEquals("Test Film", createdFilm.getName());
    }

    @Test
    void shouldNotCreateFilmWithReleaseDateBeforeCinemaBirthday() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // One day before cinema birthday
        film.setDuration(120);

        when(filmService.addFilm(any(Film.class))).thenThrow(new ValidationException("Release date cannot be earlier than 1895-12-28"));

        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldCreateFilmWithReleaseDateOnCinemaBirthday() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28)); // Cinema birthday
        film.setDuration(120);

        Film expectedFilm = new Film();
        expectedFilm.setId(1);
        expectedFilm.setName("Test Film");
        expectedFilm.setDescription("Test Description");
        expectedFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        expectedFilm.setDuration(120);

        when(filmService.addFilm(any(Film.class))).thenReturn(expectedFilm);

        Film createdFilm = filmController.createFilm(film);
        assertEquals(1, createdFilm.getId());
    }

    @Test
    void shouldUpdateFilm() {
        Film film = new Film();
        film.setId(1);
        film.setName("Updated Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        when(filmService.updateFilm(any(Film.class))).thenReturn(film);
        Film updatedFilm = filmController.updateFilm(film);
        assertEquals("Updated Film", updatedFilm.getName());
    }

    @Test
    void shouldNotUpdateNonExistentFilm() {
        Film film = new Film();
        film.setId(999); // Non-existent ID
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        when(filmService.updateFilm(any(Film.class))).thenThrow(new NotFoundException("Film with ID 999 not found"));
        assertThrows(NotFoundException.class, () -> filmController.updateFilm(film));
    }
}

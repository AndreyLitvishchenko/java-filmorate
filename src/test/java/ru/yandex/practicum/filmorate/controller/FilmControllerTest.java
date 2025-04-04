package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

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

        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldCreateFilmWithReleaseDateOnCinemaBirthday() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28)); // Cinema birthday
        film.setDuration(120);

        Film createdFilm = filmController.createFilm(film);
        assertEquals(1, createdFilm.getId());
    }

    @Test
    void shouldUpdateFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Film createdFilm = filmController.createFilm(film);

        // Update film
        createdFilm.setName("Updated Film");
        Film updatedFilm = filmController.updateFilm(createdFilm);
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

        assertThrows(NotFoundException.class, () -> filmController.updateFilm(film));
    }
}
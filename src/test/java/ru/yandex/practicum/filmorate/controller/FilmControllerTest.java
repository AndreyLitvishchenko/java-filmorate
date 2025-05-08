package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
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
import ru.yandex.practicum.filmorate.service.FilmService;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {
    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    private Film film;
    private Film popularFilm;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setId(1);
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        popularFilm = new Film();
        popularFilm.setId(2);
        popularFilm.setName("Popular Film");
        popularFilm.setDescription("Popular Film Description");
        popularFilm.setReleaseDate(LocalDate.of(2020, 5, 15));
        popularFilm.setDuration(150);
        popularFilm.setMpa(new Mpa(2, "PG"));
    }

    @Test
    void shouldCreateValidFilm() {
        when(filmService.createFilm(any(Film.class))).thenAnswer(invocation -> {
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

        when(filmService.createFilm(any(Film.class)))
                .thenThrow(new ValidationException("Release date cannot be earlier than cinema birthday"));

        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldCreateFilmWithReleaseDateOnCinemaBirthday() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28)); // Cinema birthday

        when(filmService.createFilm(any(Film.class))).thenAnswer(invocation -> {
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

        when(filmService.updateFilm(any(Film.class))).thenReturn(film);

        Film updatedFilm = filmController.updateFilm(film);
        assertEquals("Updated Film", updatedFilm.getName());
    }

    @Test
    void shouldGetFilmById() {
        when(filmService.getFilmById(1)).thenReturn(Optional.of(film));
        Film foundFilm = filmController.getFilmById(1);
        assertEquals("Test Film", foundFilm.getName());
    }

    @Test
    void shouldThrowExceptionWhenFilmNotFound() {
        when(filmService.getFilmById(999)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> filmController.getFilmById(999));
    }

    @Test
    void shouldGetAllFilms() {
        List<Film> films = List.of(film, popularFilm);
        when(filmService.getAllFilms()).thenReturn(films);

        List<Film> result = filmController.getAllFilms();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
    }

    @Test
    void shouldAddLike() {
        doNothing().when(filmService).addLike(anyInt(), anyInt());

        filmController.addLike(1, 1);

        verify(filmService).addLike(1, 1);
    }

    @Test
    void shouldRemoveLike() {
        doNothing().when(filmService).removeLike(anyInt(), anyInt());

        filmController.removeLike(1, 1);

        verify(filmService).removeLike(1, 1);
    }

    @Test
    void shouldGetPopularFilms() {
        List<Film> popularFilms = List.of(popularFilm, film);
        when(filmService.getPopularFilms(10)).thenReturn(popularFilms);

        List<Film> result = filmController.getPopularFilms(10);

        assertEquals(2, result.size());
        assertEquals("Popular Film", result.get(0).getName());
    }
}

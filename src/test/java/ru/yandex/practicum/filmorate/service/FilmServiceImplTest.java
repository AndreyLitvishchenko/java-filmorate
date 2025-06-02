package ru.yandex.practicum.filmorate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@ExtendWith(MockitoExtension.class)
class FilmServiceImplTest {

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private GenreService genreService;

    @Mock
    private MpaService mpaService;

    @Mock
    private UserService userService;

    @Mock
    private DirectorService directorService;

    @InjectMocks
    private FilmServiceImpl filmService;

    private Film film;
    private Mpa mpa;
    private List<Genre> genres;

    @BeforeEach
    void setUp() {
        mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Comedy");
        genres.add(genre);

        film = new Film();
        film.setId(1);
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(mpa);
        film.setGenres(genres);
    }

    @Test
    void shouldThrowValidationExceptionWhenReleaseDateBeforeCinemaBirthday() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class, () -> filmService.createFilm(film));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenMpaNotFound() {
        when(mpaService.getMpaById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.createFilm(film));
    }

    @Test
    void shouldGetAllFilmsWithGenres() {
        List<Film> films = List.of(film);
        List<Integer> filmIds = List.of(1);
        Map<Integer, List<Genre>> filmGenres = new HashMap<>();
        filmGenres.put(1, genres);

        when(filmStorage.findAll()).thenReturn(films);
        when(genreService.getGenresForFilms(filmIds)).thenReturn(filmGenres);
        when(directorService.getFilmDirectors(anyInt())).thenReturn(new ArrayList<>());

        List<Film> result = filmService.getAllFilms();

        assertEquals(1, result.size());
        assertEquals(genres, result.get(0).getGenres());
        verify(genreService).getGenresForFilms(filmIds);
    }

    @Test
    void shouldGetPopularFilmsWithGenres() {
        List<Film> films = List.of(film);
        List<Integer> filmIds = List.of(1);
        Map<Integer, List<Genre>> filmGenres = new HashMap<>();
        filmGenres.put(1, genres);

        when(filmStorage.getMostPopularFilms(anyInt())).thenReturn(films);
        when(genreService.getGenresForFilms(filmIds)).thenReturn(filmGenres);
        when(directorService.getFilmDirectors(anyInt())).thenReturn(new ArrayList<>());

        List<Film> result = filmService.getPopularFilms(10, null, null);

        assertEquals(1, result.size());
        assertEquals(genres, result.get(0).getGenres());
        verify(genreService).getGenresForFilms(filmIds);
    }

    @Test
    void shouldAddLike() {
        when(filmStorage.findFilmById(anyInt())).thenReturn(Optional.of(film));
        when(userService.getUserById(anyInt())).thenReturn(Optional.of(new ru.yandex.practicum.filmorate.model.User()));

        filmService.addLike(1, 1);

        verify(filmStorage).addLike(1, 1);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenFilmNotFoundForAddLike() {
        when(filmStorage.findFilmById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.addLike(999, 1));
    }
}

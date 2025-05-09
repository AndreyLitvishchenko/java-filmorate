package ru.yandex.practicum.filmorate.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.impl.GenreServiceImpl;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    @Mock
    private GenreStorage genreStorage;

    @InjectMocks
    private GenreServiceImpl genreService;

    private Genre genre;
    private List<Genre> genres;

    @BeforeEach
    void setUp() {
        genre = new Genre();
        genre.setId(1);
        genre.setName("Comedy");

        genres = new ArrayList<>();
        genres.add(genre);
    }

    @Test
    void shouldGetAllGenres() {
        when(genreStorage.findAll()).thenReturn(genres);

        List<Genre> result = genreService.getAllGenres();

        assertEquals(1, result.size());
        assertEquals("Comedy", result.get(0).getName());
    }

    @Test
    void shouldGetGenreById() {
        when(genreStorage.findGenreById(1)).thenReturn(Optional.of(genre));

        Optional<Genre> result = genreService.getGenreById(1);

        assertTrue(result.isPresent());
        assertEquals("Comedy", result.get().getName());
    }

    @Test
    void shouldGetFilmGenres() {
        when(genreStorage.getFilmGenres(1)).thenReturn(genres);

        List<Genre> result = genreService.getFilmGenres(1);

        assertEquals(1, result.size());
        assertEquals("Comedy", result.get(0).getName());
    }

    @Test
    void shouldGetGenresForFilms() {
        List<Integer> filmIds = List.of(1, 2);
        Map<Integer, List<Genre>> genresMap = new HashMap<>();
        genresMap.put(1, genres);

        when(genreStorage.getGenresForFilms(filmIds)).thenReturn(genresMap);

        Map<Integer, List<Genre>> result = genreService.getGenresForFilms(filmIds);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(1));
        assertEquals(1, result.get(1).size());
    }

    @Test
    void shouldAddGenresToFilm() {
        when(genreStorage.findGenreById(1)).thenReturn(Optional.of(genre));

        genreService.addGenresToFilm(1, genres);

        verify(genreStorage).addFilmGenres(1, genres);
    }

    @Test
    void shouldUpdateFilmGenres() {
        when(genreStorage.findGenreById(1)).thenReturn(Optional.of(genre));

        genreService.updateFilmGenres(1, genres);

        verify(genreStorage).updateFilmGenres(1, genres);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenGenreNotFound() {
        Genre unknownGenre = new Genre();
        unknownGenre.setId(999);
        List<Genre> genresWithUnknown = List.of(unknownGenre);

        when(genreStorage.findGenreById(999)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> genreService.addGenresToFilm(1, genresWithUnknown));
    }
}

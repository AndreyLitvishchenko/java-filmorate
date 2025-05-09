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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

@ExtendWith(MockitoExtension.class)
class GenreControllerTest {
    @Mock
    private GenreService genreService;

    @InjectMocks
    private GenreController genreController;

    private Genre genre;
    private List<Genre> genreList;

    @BeforeEach
    void setUp() {
        genre = new Genre(1, "Комедия");
        genreList = List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик"));
    }

    @Test
    void shouldGetAllGenres() {
        when(genreService.getAllGenres()).thenReturn(genreList);

        List<Genre> result = genreController.getAllGenres();

        assertEquals(6, result.size());
        assertEquals("Комедия", result.get(0).getName());
        assertEquals("Драма", result.get(1).getName());
    }

    @Test
    void shouldGetGenreById() {
        when(genreService.getGenreById(1)).thenReturn(Optional.of(genre));

        Genre result = genreController.getGenreById(1);

        assertEquals(1, result.getId());
        assertEquals("Комедия", result.getName());
    }

    @Test
    void shouldThrowExceptionWhenGenreNotFound() {
        when(genreService.getGenreById(999)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> genreController.getGenreById(999));
    }
}

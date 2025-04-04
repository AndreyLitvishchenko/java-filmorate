package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.util.TestJsonUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return empty film list when no films exist")
    void shouldReturnEmptyFilmList() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("Should create a valid film")
    void shouldCreateFilm() throws Exception {
        String filmJson = TestJsonUtils.readJsonFromFile("json/valid-film.json");
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filmJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Film"));
    }

    @Test
    @DisplayName("Should not create film with empty name")
    void shouldNotCreateFilmWithEmptyName() throws Exception {
        String filmJson = TestJsonUtils.readJsonFromFile("json/film-empty-name.json");
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should not create film with negative duration")
    void shouldNotCreateFilmWithNegativeDuration() throws Exception {
        String filmJson = TestJsonUtils.readJsonFromFile("json/film-negative-duration.json");
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should not update non-existent film")
    void shouldNotUpdateNonExistentFilm() throws Exception {
        String filmJson = TestJsonUtils.readJsonFromFile("json/nonexistent-film.json");
        mockMvc.perform(put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filmJson))
                .andExpect(status().isNotFound());
    }
}
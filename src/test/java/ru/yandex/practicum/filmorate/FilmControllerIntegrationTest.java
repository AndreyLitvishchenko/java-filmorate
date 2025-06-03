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

    @Test
    @DisplayName("Should get film by id")
    void shouldGetFilmById() throws Exception {
        String filmJson = TestJsonUtils.readJsonFromFile("json/valid-film.json");
        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        int filmId = Integer
                .parseInt(response.substring(response.indexOf("\"id\":") + 5, response.indexOf(",")));

        mockMvc.perform(get("/films/" + filmId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(filmId))
                .andExpect(jsonPath("$.name").value("Test Film"));
    }

    @Test
    @DisplayName("Should return 404 when film not found")
    void shouldReturn404WhenFilmNotFound() throws Exception {
        mockMvc.perform(get("/films/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should add like to film")
    void shouldAddLikeToFilm() throws Exception {
        String userJson = TestJsonUtils.readJsonFromFile("json/valid-user.json");
        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        int userId = Integer
                .parseInt(userResponse.substring(userResponse.indexOf("\"id\":") + 5,
                        userResponse.indexOf(",")));

        String filmJson = TestJsonUtils.readJsonFromFile("json/valid-film.json");
        String filmResponse = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        int filmId = Integer
                .parseInt(filmResponse.substring(filmResponse.indexOf("\"id\":") + 5,
                        filmResponse.indexOf(",")));

        mockMvc.perform(put("/films/" + filmId + "/like/" + userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(filmId));
    }

    @Test
    @DisplayName("Should remove like from film")
    void shouldRemoveLikeFromFilm() throws Exception {
        String userJson = TestJsonUtils.readJsonFromFile("json/valid-user.json");
        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        int userId = Integer
                .parseInt(userResponse.substring(userResponse.indexOf("\"id\":") + 5,
                        userResponse.indexOf(",")));

        String filmJson = TestJsonUtils.readJsonFromFile("json/valid-film.json");
        String filmResponse = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        int filmId = Integer
                .parseInt(filmResponse.substring(filmResponse.indexOf("\"id\":") + 5,
                        filmResponse.indexOf(",")));

        mockMvc.perform(put("/films/" + filmId + "/like/" + userId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/films/" + filmId + "/like/" + userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(filmId));
    }
}

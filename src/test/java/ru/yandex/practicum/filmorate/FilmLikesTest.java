package ru.yandex.practicum.filmorate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.util.TestJsonUtils;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmLikesTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("Should be able to get film by id")
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
        }

        @Test
        @DisplayName("Should get popular films")
        void shouldGetPopularFilms() throws Exception {
                String filmJson1 = TestJsonUtils.readJsonFromFile("json/valid-film.json");
                String filmResponse1 = mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson1))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                int filmId1 = Integer
                                .parseInt(filmResponse1.substring(filmResponse1.indexOf("\"id\":") + 5,
                                                filmResponse1.indexOf(",")));

                String filmJson2 = "{\n" +
                                "  \"name\": \"Test Film 2\",\n" +
                                "  \"description\": \"Test Description 2\",\n" +
                                "  \"releaseDate\": \"2000-01-01\",\n" +
                                "  \"duration\": 120,\n" +
                                "  \"mpa\": {\n" +
                                "    \"id\": 1,\n" +
                                "    \"name\": \"G\"\n" +
                                "  }\n" +
                                "}";
                String filmResponse2 = mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson2))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                int filmId2 = Integer
                                .parseInt(filmResponse2.substring(filmResponse2.indexOf("\"id\":") + 5,
                                                filmResponse2.indexOf(",")));

                String userJson1 = TestJsonUtils.readJsonFromFile("json/valid-user.json");
                String userResponse1 = mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson1))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                int userId1 = Integer
                                .parseInt(userResponse1.substring(userResponse1.indexOf("\"id\":") + 5,
                                                userResponse1.indexOf(",")));

                String userJson2 = "{\n" +
                                "  \"email\": \"test2@example.com\",\n" +
                                "  \"login\": \"testuser2\",\n" +
                                "  \"name\": \"Test User 2\",\n" +
                                "  \"birthday\": \"2000-01-01\"\n" +
                                "}";
                String userResponse2 = mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson2))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                int userId2 = Integer
                                .parseInt(userResponse2.substring(userResponse2.indexOf("\"id\":") + 5,
                                                userResponse2.indexOf(",")));
                mockMvc.perform(put("/films/" + filmId1 + "/like/" + userId1))
                                .andExpect(status().isOk());

                mockMvc.perform(put("/films/" + filmId2 + "/like/" + userId1))
                                .andExpect(status().isOk());

                mockMvc.perform(put("/films/" + filmId2 + "/like/" + userId2))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/films/popular"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(filmId2))
                                .andExpect(jsonPath("$[1].id").value(filmId1));
        }
}

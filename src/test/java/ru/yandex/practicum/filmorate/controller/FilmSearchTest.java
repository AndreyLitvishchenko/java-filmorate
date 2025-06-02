package ru.yandex.practicum.filmorate.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FilmSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private Director director1;
    private Director director2;
    private Film film1;
    private Film film2;
    private Film film3;
    private User user;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.execute("DELETE FROM directors_films");
        jdbcTemplate.execute("DELETE FROM film_genres");
        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM review_reactions");
        jdbcTemplate.execute("DELETE FROM reviews");
        jdbcTemplate.execute("DELETE FROM events");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM directors");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE directors ALTER COLUMN director_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
    }

    @BeforeEach
    void setUp() throws Exception {
        user = new User();
        user.setEmail("user@test.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user = objectMapper.readValue(
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                        .andReturn().getResponse().getContentAsString(),
                User.class);

        director1 = Director.builder().name("Никита Михалков").build();
        director1 = objectMapper.readValue(
                mockMvc.perform(post("/directors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(director1)))
                        .andReturn().getResponse().getContentAsString(),
                Director.class);

        director2 = Director.builder().name("Алексей Балабанов").build();
        director2 = objectMapper.readValue(
                mockMvc.perform(post("/directors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(director2)))
                        .andReturn().getResponse().getContentAsString(),
                Director.class);

        film1 = new Film();
        film1.setName("Крадущийся тигр, затаившийся дракон");
        film1.setDescription("Фильм о восточных единоборствах");
        film1.setReleaseDate(LocalDate.of(2000, 7, 8));
        film1.setDuration(120);
        film1.setMpa(new Mpa(1, "G"));
        film1.setDirectors(List.of(director1));

        film2 = new Film();
        film2.setName("Крадущийся в ночи");
        film2.setDescription("Триллер");
        film2.setReleaseDate(LocalDate.of(2010, 5, 15));
        film2.setDuration(110);
        film2.setMpa(new Mpa(1, "G"));
        film2.setDirectors(List.of(director2));

        film3 = new Film();
        film3.setName("Брат");
        film3.setDescription("Культовый фильм");
        film3.setReleaseDate(LocalDate.of(1997, 5, 17));
        film3.setDuration(96);
        film3.setMpa(new Mpa(1, "G"));
        film3.setDirectors(List.of(director2));
    }

    @Test
    void shouldSearchFilmsByTitle() throws Exception {
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film1)));
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film2)));
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film3)));

        mockMvc.perform(get("/films/search?query=крад&by=title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(
                        "Крадущийся тигр, затаившийся дракон",
                        "Крадущийся в ночи"
                )));
    }

    @Test
    void shouldReturnEmptyListWhenNoMatches() throws Exception {
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film1)));

        mockMvc.perform(get("/films/search?query=несуществующий&by=title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldSearchFilmsSimpleTest() throws Exception {
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film1)));

        mockMvc.perform(get("/films/search?query=тигр&by=title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Крадущийся тигр, затаившийся дракон")));
    }
}

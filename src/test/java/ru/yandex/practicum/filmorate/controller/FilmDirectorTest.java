package ru.yandex.practicum.filmorate.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = { "/schema.sql", "/data.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FilmDirectorTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        private Director director1;
        private Director director2;
        private Film film1;
        private Film film2;
        private User user;

        @BeforeEach
        void setUp() throws Exception {
                // Создаем пользователя для лайков
                user = new User();
                user.setEmail("user@test.com");
                user.setLogin("testuser");
                user.setName("Test User");
                user.setBirthday(LocalDate.of(1990, 1, 1));

                String userResponse = mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                                .andReturn().getResponse().getContentAsString();
                user = objectMapper.readValue(userResponse, User.class);

                // Создаем режиссеров
                director1 = Director.builder().name("Steven Spielberg").build();
                String dirResponse1 = mockMvc.perform(post("/directors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(director1)))
                                .andReturn().getResponse().getContentAsString();
                director1 = objectMapper.readValue(dirResponse1, Director.class);

                director2 = Director.builder().name("Christopher Nolan").build();
                String dirResponse2 = mockMvc.perform(post("/directors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(director2)))
                                .andReturn().getResponse().getContentAsString();
                director2 = objectMapper.readValue(dirResponse2, Director.class);

                // Создаем фильмы
                film1 = new Film();
                film1.setName("Jurassic Park");
                film1.setDescription("Dinosaurs come to life");
                film1.setReleaseDate(LocalDate.of(1993, 6, 11));
                film1.setDuration(127);
                film1.setMpa(new Mpa(1, "G"));
                film1.setDirectors(List.of(director1));

                film2 = new Film();
                film2.setName("Inception");
                film2.setDescription("Dream within a dream");
                film2.setReleaseDate(LocalDate.of(2010, 7, 16));
                film2.setDuration(148);
                film2.setMpa(new Mpa(1, "G"));
                film2.setDirectors(List.of(director2));
        }

        @Test
        void shouldGetFilmsByDirectorSortedByLikes() throws Exception {
                // Создаем фильмы
                String filmResponse1 = mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(film1)))
                                .andReturn().getResponse().getContentAsString();
                Film createdFilm1 = objectMapper.readValue(filmResponse1, Film.class);

                Film film3 = new Film();
                film3.setName("E.T.");
                film3.setDescription("Alien friend");
                film3.setReleaseDate(LocalDate.of(1982, 6, 11));
                film3.setDuration(115);
                film3.setMpa(new Mpa(1, "G"));
                film3.setDirectors(List.of(director1));

                String filmResponse3 = mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(film3)))
                                .andReturn().getResponse().getContentAsString();
                Film createdFilm3 = objectMapper.readValue(filmResponse3, Film.class);

                // Добавляем лайки
                mockMvc.perform(put("/films/" + createdFilm1.getId() + "/like/" + user.getId()));

                // Получаем фильмы режиссера отсортированные по лайкам
                mockMvc.perform(get("/films/director/" + director1.getId() + "?sortBy=likes"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].name").value("Jurassic Park"))
                                .andExpect(jsonPath("$[1].name").value("E.T."));
        }

        @Test
        void shouldGetFilmsByDirectorSortedByYear() throws Exception {
                // Создаем фильмы
                mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(film1)));

                Film film3 = new Film();
                film3.setName("E.T.");
                film3.setDescription("Alien friend");
                film3.setReleaseDate(LocalDate.of(1982, 6, 11));
                film3.setDuration(115);
                film3.setMpa(new Mpa(1, "G"));
                film3.setDirectors(List.of(director1));

                mockMvc.perform(post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(film3)));

                // Получаем фильмы режиссера отсортированные по году
                mockMvc.perform(get("/films/director/" + director1.getId() + "?sortBy=year"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].name").value("E.T."))
                                .andExpect(jsonPath("$[1].name").value("Jurassic Park"));
        }
}

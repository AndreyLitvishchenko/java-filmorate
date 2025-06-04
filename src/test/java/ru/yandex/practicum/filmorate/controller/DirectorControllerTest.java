package ru.yandex.practicum.filmorate.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = { "/schema.sql", "/data.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DirectorControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        private Director testDirector;

        @BeforeEach
        void setUp() {
                // Очищаем таблицы связанные с режиссерами
                jdbcTemplate.update("DELETE FROM directors_films");
                jdbcTemplate.update("DELETE FROM directors");
                jdbcTemplate.update("ALTER TABLE directors ALTER COLUMN director_id RESTART WITH 1");

                testDirector = Director.builder()
                                .name("Steven Spielberg")
                                .build();
        }

        @Test
        void shouldCreateDirector() throws Exception {
                mockMvc.perform(post("/directors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testDirector)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.name").value("Steven Spielberg"));
        }

        @Test
        void shouldGetAllDirectors() throws Exception {
                // Создаем директора
                mockMvc.perform(post("/directors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testDirector)));

                mockMvc.perform(get("/directors"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].name").value("Steven Spielberg"));
        }

        @Test
        void shouldGetDirectorById() throws Exception {
                // Создаем директора
                String response = mockMvc.perform(post("/directors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testDirector)))
                                .andReturn().getResponse().getContentAsString();

                Director createdDirector = objectMapper.readValue(response, Director.class);

                mockMvc.perform(get("/directors/" + createdDirector.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(createdDirector.getId()))
                                .andExpect(jsonPath("$.name").value("Steven Spielberg"));
        }

        @Test
        void shouldUpdateDirector() throws Exception {
                // Создаем директора
                String response = mockMvc.perform(post("/directors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testDirector)))
                                .andReturn().getResponse().getContentAsString();

                Director createdDirector = objectMapper.readValue(response, Director.class);
                createdDirector.setName("Christopher Nolan");

                mockMvc.perform(put("/directors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createdDirector)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Christopher Nolan"));
        }

        @Test
        void shouldDeleteDirector() throws Exception {
                // Создаем директора
                String response = mockMvc.perform(post("/directors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testDirector)))
                                .andReturn().getResponse().getContentAsString();

                Director createdDirector = objectMapper.readValue(response, Director.class);

                mockMvc.perform(delete("/directors/" + createdDirector.getId()))
                                .andExpect(status().isOk());

                // Проверяем что директор удален - должен вернуться список без этого директора
                mockMvc.perform(get("/directors"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldReturnBadRequestForInvalidDirector() throws Exception {
                Director invalidDirector = Director.builder()
                                .name("")
                                .build();

                mockMvc.perform(post("/directors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDirector)))
                                .andExpect(status().isBadRequest());
        }
}

package ru.yandex.practicum.filmorate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
class UserFriendsTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("Should be able to get user by id")
        void shouldGetUserById() throws Exception {
                String userJson = TestJsonUtils.readJsonFromFile("json/valid-user.json");
                String response = mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                int userId = Integer
                                .parseInt(response.substring(response.indexOf("\"id\":") + 5, response.indexOf(",")));

                mockMvc.perform(get("/users/" + userId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(userId))
                                .andExpect(jsonPath("$.name").value("Test User"));
        }

        @Test
        @DisplayName("Should return 404 when user not found")
        void shouldReturn404WhenUserNotFound() throws Exception {
                mockMvc.perform(get("/users/999"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should add friend")
        void shouldAddFriend() throws Exception {
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

                mockMvc.perform(put("/users/" + userId1 + "/friends/" + userId2))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/users/" + userId1 + "/friends"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(userId2));

                mockMvc.perform(get("/users/" + userId2 + "/friends"))
                                .andExpect(status().isOk())
                                .andExpect(content().json("[]"));
        }

        @Test
        @DisplayName("Should remove friend")
        void shouldRemoveFriend() throws Exception {
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

                mockMvc.perform(put("/users/" + userId1 + "/friends/" + userId2))
                                .andExpect(status().isOk());

                mockMvc.perform(delete("/users/" + userId1 + "/friends/" + userId2))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/users/" + userId1 + "/friends"))
                                .andExpect(status().isOk())
                                .andExpect(content().json("[]"));

                mockMvc.perform(get("/users/" + userId2 + "/friends"))
                                .andExpect(status().isOk())
                                .andExpect(content().json("[]"));
        }

        @Test
        @DisplayName("Should get common friends")
        void shouldGetCommonFriends() throws Exception {
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

                String userJson3 = "{\n" +
                                "  \"email\": \"test3@example.com\",\n" +
                                "  \"login\": \"testuser3\",\n" +
                                "  \"name\": \"Test User 3\",\n" +
                                "  \"birthday\": \"2000-01-01\"\n" +
                                "}";
                String userResponse3 = mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson3))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                int userId3 = Integer
                                .parseInt(userResponse3.substring(userResponse3.indexOf("\"id\":") + 5,
                                                userResponse3.indexOf(",")));

                mockMvc.perform(put("/users/" + userId1 + "/friends/" + userId3))
                                .andExpect(status().isOk());

                mockMvc.perform(put("/users/" + userId2 + "/friends/" + userId3))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/users/" + userId1 + "/friends/common/" + userId2))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(userId3));
        }
}

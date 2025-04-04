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
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return empty user list when no users exist")
    void shouldReturnEmptyUserList() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("Should create a valid user")
    void shouldCreateUser() throws Exception {
        String userJson = TestJsonUtils.readJsonFromFile("json/valid-user.json");
        
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @DisplayName("Should not create user with empty email")
    void shouldNotCreateUserWithEmptyEmail() throws Exception {
        String userJson = TestJsonUtils.readJsonFromFile("json/user-empty-email.json");
        
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should not create user with invalid email")
    void shouldNotCreateUserWithInvalidEmail() throws Exception {
        String userJson = TestJsonUtils.readJsonFromFile("json/user-invalid-email.json");
        
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should not create user with empty login")
    void shouldNotCreateUserWithEmptyLogin() throws Exception {
        String userJson = TestJsonUtils.readJsonFromFile("json/user-empty-login.json");
        
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should not create user with spaces in login")
    void shouldNotCreateUserWithSpacesInLogin() throws Exception {
        String userJson = TestJsonUtils.readJsonFromFile("json/user-login-with-spaces.json");
        
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should not create user with future birthday")
    void shouldNotCreateUserWithFutureBirthday() throws Exception {
        String userJson = TestJsonUtils.readJsonFromFile("json/user-future-birthday.json");
        
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should create user with empty name and use login as name")
    void shouldCreateUserWithEmptyNameAndUseLoginAsName() throws Exception {
        String userJson = TestJsonUtils.readJsonFromFile("json/user-empty-name.json");
        
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testuser"));
    }
}
package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void shouldCreateValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.createUser(user);
        assertEquals(1, createdUser.getId());
        assertEquals("Test User", createdUser.getName());
    }

    @Test
    void shouldCreateUserWithEmptyNameAndSetLoginAsName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.createUser(user);
        assertEquals("testuser", createdUser.getName());
    }

    @Test
    void shouldCreateUserWithNullNameAndSetLoginAsName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName(null);
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.createUser(user);
        assertEquals("testuser", createdUser.getName());
    }

    @Test
    void shouldUpdateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);

        createdUser.setName("Updated User");
        User updatedUser = userController.updateUser(createdUser);
        assertEquals("Updated User", updatedUser.getName());
    }
}
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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
    void shouldNotCreateUserWithEmptyEmail() {
        User user = new User();
        user.setEmail("");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldNotCreateUserWithEmptyLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldNotCreateUserWithLoginContainingSpaces() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test user");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
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
    void shouldNotCreateUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.now().plusDays(1)); // Future date

        assertThrows(ValidationException.class, () -> userController.createUser(user));
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

    @Test
    void shouldNotUpdateNonExistentUser() {
        User user = new User();
        user.setId(999); // Non-existent ID
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(NotFoundException.class, () -> userController.updateUser(user));
    }
}
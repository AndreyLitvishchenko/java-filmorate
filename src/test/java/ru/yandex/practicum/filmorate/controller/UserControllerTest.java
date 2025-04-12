package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldCreateValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setEmail("test@example.com");
        expectedUser.setLogin("testuser");
        expectedUser.setName("Test User");
        expectedUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.addUser(any(User.class))).thenReturn(expectedUser);

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

        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setEmail("test@example.com");
        expectedUser.setLogin("testuser");
        expectedUser.setName("testuser");
        expectedUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.addUser(any(User.class))).thenReturn(expectedUser);

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

        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setEmail("test@example.com");
        expectedUser.setLogin("testuser");
        expectedUser.setName("testuser");
        expectedUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.addUser(any(User.class))).thenReturn(expectedUser);
        User createdUser = userController.createUser(user);
        assertEquals("testuser", createdUser.getName());
    }

    @Test
    void shouldUpdateUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Updated User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.updateUser(any(User.class))).thenReturn(user);
        User updatedUser = userController.updateUser(user);
        assertEquals("Updated User", updatedUser.getName());
    }
}

package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldCreateValidUser() {
        when(userStorage.create(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1);
            return u;
        });

        User createdUser = userController.createUser(user);
        assertEquals(1, createdUser.getId());
        assertEquals("Test User", createdUser.getName());
    }

    @Test
    void shouldCreateUserWithEmptyNameAndSetLoginAsName() {
        user.setName("");

        when(userStorage.create(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1);
            return u;
        });

        User createdUser = userController.createUser(user);
        assertEquals("testuser", createdUser.getName());
    }

    @Test
    void shouldCreateUserWithNullNameAndSetLoginAsName() {
        user.setName(null);

        when(userStorage.create(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1);
            return u;
        });

        User createdUser = userController.createUser(user);
        assertEquals("testuser", createdUser.getName());
    }

    @Test
    void shouldUpdateUser() {
        user.setId(1);
        user.setName("Updated User");

        when(userStorage.update(any(User.class))).thenReturn(user);

        User updatedUser = userController.updateUser(user);
        assertEquals("Updated User", updatedUser.getName());
    }
}

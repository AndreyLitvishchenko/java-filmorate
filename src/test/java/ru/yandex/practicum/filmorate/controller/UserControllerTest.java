package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;
    private User friend;
    private User commonFriend;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        friend = new User();
        friend.setId(2);
        friend.setEmail("friend@example.com");
        friend.setLogin("friend");
        friend.setName("Friend User");
        friend.setBirthday(LocalDate.of(1999, 5, 15));

        commonFriend = new User();
        commonFriend.setId(3);
        commonFriend.setEmail("common@example.com");
        commonFriend.setLogin("common");
        commonFriend.setName("Common Friend");
        commonFriend.setBirthday(LocalDate.of(1995, 10, 20));
    }

    @Test
    void shouldCreateValidUser() {
        when(userService.createUser(any(User.class))).thenAnswer(invocation -> {
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

        when(userService.createUser(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1);
            if (u.getName() == null || u.getName().isBlank()) {
                u.setName(u.getLogin());
            }
            return u;
        });

        User createdUser = userController.createUser(user);
        assertEquals("testuser", createdUser.getName());
    }

    @Test
    void shouldCreateUserWithNullNameAndSetLoginAsName() {
        user.setName(null);

        when(userService.createUser(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1);
            if (u.getName() == null || u.getName().isBlank()) {
                u.setName(u.getLogin());
            }
            return u;
        });

        User createdUser = userController.createUser(user);
        assertEquals("testuser", createdUser.getName());
    }

    @Test
    void shouldUpdateUser() {
        user.setId(1);
        user.setName("Updated User");

        when(userService.updateUser(any(User.class))).thenReturn(user);

        User updatedUser = userController.updateUser(user);
        assertEquals("Updated User", updatedUser.getName());
    }

    @Test
    void shouldGetUserById() {
        user.setId(1);
        when(userService.getUserById(1)).thenReturn(Optional.of(user));

        User foundUser = userController.getUserById(1);
        assertEquals(1, foundUser.getId());
        assertEquals("Test User", foundUser.getName());
    }

    @Test
    void shouldGetAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(friend);

        when(userService.getAllUsers()).thenReturn(users);

        List<User> result = userController.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
    }

    @Test
    void shouldAddFriend() {
        doNothing().when(userService).addFriend(anyInt(), anyInt());

        userController.addFriend(1, 2);

        verify(userService).addFriend(1, 2);
    }

    @Test
    void shouldRemoveFriend() {
        doNothing().when(userService).removeFriend(anyInt(), anyInt());

        userController.removeFriend(1, 2);

        verify(userService).removeFriend(1, 2);
    }

    @Test
    void shouldGetFriends() {
        List<User> friends = List.of(friend);

        when(userService.getFriends(1)).thenReturn(friends);

        List<User> result = userController.getFriends(1);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getId());
        assertEquals("Friend User", result.get(0).getName());
    }

    @Test
    void shouldGetCommonFriends() {
        List<User> commonFriends = List.of(commonFriend);

        when(userService.getCommonFriends(1, 2)).thenReturn(commonFriends);

        List<User> result = userController.getCommonFriends(1, 2);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getId());
        assertEquals("Common Friend", result.get(0).getName());
    }
}

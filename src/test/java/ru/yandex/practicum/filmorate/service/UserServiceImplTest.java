package ru.yandex.practicum.filmorate.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setEmail("test@test.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldCreateUserWithValidData() {
        when(userStorage.create(any(User.class))).thenReturn(user);

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        verify(userStorage).create(any(User.class));
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsEmpty() {
        user.setName("");
        when(userStorage.create(any(User.class))).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals(user.getLogin(), result.getName());
    }

    @Test
    void shouldThrowValidationExceptionWhenLoginContainsSpaces() {
        user.setLogin("invalid login");

        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    void shouldThrowValidationExceptionWhenBirthdayInFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    void shouldUpdateUserWithValidData() {
        when(userStorage.findUserById(anyInt())).thenReturn(Optional.of(user));
        when(userStorage.update(any(User.class))).thenReturn(user);

        User result = userService.updateUser(user);

        assertNotNull(result);
        verify(userStorage).update(any(User.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userStorage.findUserById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(user));
    }

    @Test
    void shouldAddFriend() {
        User friend = new User();
        friend.setId(2);

        when(userStorage.findUserById(1)).thenReturn(Optional.of(user));
        when(userStorage.findUserById(2)).thenReturn(Optional.of(friend));

        userService.addFriend(1, 2);

        verify(userStorage).addFriend(1, 2);
    }

    @Test
    void shouldGetFriends() {
        User friend = new User();
        friend.setId(2);

        when(userStorage.findUserById(1)).thenReturn(Optional.of(user));
        when(userStorage.getFriends(1)).thenReturn(List.of(friend));

        List<User> friends = userService.getFriends(1);

        assertEquals(1, friends.size());
        assertEquals(2, friends.get(0).getId());
    }

    @Test
    void shouldGetCommonFriends() {
        User friend = new User();
        friend.setId(3);

        when(userStorage.findUserById(1)).thenReturn(Optional.of(user));
        when(userStorage.findUserById(2)).thenReturn(Optional.of(new User()));
        when(userStorage.getCommonFriends(1, 2)).thenReturn(List.of(friend));

        List<User> commonFriends = userService.getCommonFriends(1, 2);

        assertEquals(1, commonFriends.size());
        assertEquals(3, commonFriends.get(0).getId());
    }
}

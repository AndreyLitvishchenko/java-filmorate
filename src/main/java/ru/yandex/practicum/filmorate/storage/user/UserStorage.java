package ru.yandex.practicum.filmorate.storage.user;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    Optional<User> getUserById(int id);

    void deleteUser(int id);
}

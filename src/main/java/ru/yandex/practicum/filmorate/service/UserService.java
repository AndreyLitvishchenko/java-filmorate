package ru.yandex.practicum.filmorate.service;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.User;

public interface UserService {

    User createUser(User user);

    User updateUser(User user);

    Optional<User> getUserById(int id);

    List<User> getAllUsers();

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int otherUserId);

    void removeUser(int id);
}

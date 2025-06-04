package ru.yandex.practicum.filmorate.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Override
    public User createUser(User user) {
        validateUser(user);
        setNameIfEmpty(user);

        User createdUser = userStorage.create(user);

        log.info("User created: {}", createdUser);
        return createdUser;
    }

    @Override
    public User updateUser(User user) {
        validateUser(user);
        validateUserExists(user.getId());
        setNameIfEmpty(user);

        User updatedUser = userStorage.update(user);

        log.info("User updated: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public Optional<User> getUserById(int id) {
        return userStorage.findUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    @Override
    public void addFriend(int userId, int friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        userStorage.addFriend(userId, friendId);
        addEvent(userId, friendId, "FRIEND", "ADD");
        log.info("User {} added friend {}", userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        userStorage.removeFriend(userId, friendId);
        addEvent(userId, friendId, "FRIEND", "REMOVE");
        log.info("User {} removed friend {}", userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        validateUserExists(userId);

        return userStorage.getFriends(userId);
    }

    @Override
    public void removeUser(int id) {
        validateUserExists(id);
        userStorage.removeUser(id);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        validateUserExists(userId);
        validateUserExists(otherUserId);

        return userStorage.getCommonFriends(userId, otherUserId);
    }

    @Override
    public void addEvent(int userId, int entityId, String eventType, String eventOperation) {
        eventStorage.add(Instant.now(), userId, entityId, eventType, eventOperation);
    }

    @Override
    public List<Event> getEvents(int id) {
        validateUserExists(id);
        return eventStorage.get(id);
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login cannot contain spaces");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday cannot be in the future");
        }
    }

    private void validateUserExists(int userId) {
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}

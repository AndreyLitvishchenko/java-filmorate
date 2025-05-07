package ru.yandex.practicum.filmorate.storage.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public List<User> getAllUsers() {
        log.debug("Getting all users. Total count: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.debug("User added: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("User with ID {} not found", user.getId());
            throw new NotFoundException("User with ID " + user.getId() + " not found");
        }
        users.put(user.getId(), user);
        log.debug("User updated: {}", user);
        return user;
    }

    @Override
    public Optional<User> getUserById(int id) {
        if (!users.containsKey(id)) {
            log.warn("User with ID {} not found", id);
            return Optional.empty();
        }
        return Optional.of(users.get(id));
    }

    @Override
    public void deleteUser(int id) {
        if (!users.containsKey(id)) {
            log.warn("User with ID {} not found", id);
            throw new NotFoundException("User with ID " + id + " not found");
        }
        users.remove(id);
        log.debug("User with ID {} deleted", id);
    }
}

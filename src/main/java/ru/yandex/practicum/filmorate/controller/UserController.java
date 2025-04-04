package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Getting all users. Total count: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        validateUser(user);
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("User added: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user.getId() <= 0 || !users.containsKey(user.getId())) {
            throw new ValidationException("User with ID " + user.getId() + " not found");
        }
        validateUser(user);
        users.put(user.getId(), user);
        log.info("User updated: {}", user);
        return user;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email cannot be empty");
        }
        
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email must contain @ symbol");
        }
        
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Login cannot be empty");
        }
        
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login cannot contain spaces");
        }
        
        if (user.getBirthday() == null) {
            throw new ValidationException("Birthday cannot be null");
        }
        
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday cannot be in the future");
        }
        
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("User name is empty, using login instead: {}", user.getLogin());
        }
    }
} 
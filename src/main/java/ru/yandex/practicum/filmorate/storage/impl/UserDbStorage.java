package ru.yandex.practicum.filmorate.storage.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                    new String[] { "user_id" });
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
            },
                keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("User created: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        int rowsUpdated = jdbcTemplate.update(
                "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("User with ID " + user.getId() + " not found");
        }
        log.info("User updated: {}", user);
        return user;
    }

    @Override
    public Optional<User> findUserById(int id) {
        List<User> users = jdbcTemplate.query(
                "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?",
                userMapper,
                id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(
                "SELECT user_id, email, login, name, birthday FROM users",
                userMapper);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        validUserExists(userId);
        validUserExists(friendId);
        jdbcTemplate.update(
                "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)",
                userId, friendId);
        log.info("User {} added friend {}", userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        validUserExists(userId);
        validUserExists(friendId);
        jdbcTemplate.update(
                "DELETE FROM friends WHERE user_id = ? AND friend_id = ?",
                userId, friendId);
        log.info("User {} removed friend {}", userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        validUserExists(userId);
        return jdbcTemplate.query(
                "SELECT u.user_id, u.email, u.login, u.name, u.birthday FROM users u " +
                        "JOIN friends f ON u.user_id = f.friend_id " +
                        "WHERE f.user_id = ?",
                userMapper,
                userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        validUserExists(userId);
        validUserExists(otherUserId);
        return jdbcTemplate.query(
                "SELECT u.user_id, u.email, u.login, u.name, u.birthday FROM users u " +
                        "WHERE u.user_id IN (" +
                        "  SELECT f1.friend_id FROM friends f1 " +
                        "  JOIN friends f2 ON f1.friend_id = f2.friend_id " +
                        "  WHERE f1.user_id = ? AND f2.user_id = ?" +
                        ")",
                userMapper,
                userId, otherUserId);
    }

    @Override
    public void removeUser(int id) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id);
        log.info("Removed user id = {}" , id);
    }

    private void validUserExists(int userId) {
        if (!jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?)",
                Boolean.class,
                userId)) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
    }
}

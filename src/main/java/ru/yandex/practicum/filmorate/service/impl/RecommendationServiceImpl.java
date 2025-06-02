package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Override
    public List<Film> getRecommendations(int userId) {
        validateUserExists(userId);

        Map<Integer, Set<Integer>> userLikes = getAllUserLikes();
        int mostSimilarUserId = findMostSimilarUser(userId, userLikes);

        if (mostSimilarUserId == -1) {
            log.info("No similar users found for user {}", userId);
            return Collections.emptyList();
        }

        Set<Integer> recommendedFilmIds = getRecommendedFilmIds(userId, mostSimilarUserId, userLikes);

        return mapFilmIdsToFilms(recommendedFilmIds);
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        List<Integer> userLikes = userStorage.getLikedFilms(userId);
        List<Integer> friendLikes = userStorage.getLikedFilms(friendId);

        Set<Integer> commonFilmIds = findCommonFilmIds(userLikes, friendLikes);

        return mapFilmIdsToFilms(commonFilmIds).stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikesCount(), f1.getLikesCount()))
                .collect(Collectors.toList());
    }

    private Map<Integer, Set<Integer>> getAllUserLikes() {
        Map<Integer, Set<Integer>> userLikes = new HashMap<>();
        userStorage.findAll().forEach(user ->
                userLikes.put(user.getId(), new HashSet<>(filmStorage.getLikedFilms(user.getId())))
        );
        return userLikes;
    }

    private int findMostSimilarUser(int userId, Map<Integer, Set<Integer>> userLikes) {
        Set<Integer> currentUserLikes = userLikes.get(userId);
        return userLikes.entrySet().stream()
                .filter(entry -> entry.getKey() != userId)
                .max(Comparator.comparingInt(entry ->
                        getIntersectionSize(currentUserLikes, entry.getValue())))
                .map(Map.Entry::getKey)
                .orElse(-1);
    }

    private int getIntersectionSize(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection.size();
    }

    private Set<Integer> getRecommendedFilmIds(int userId, int similarUserId, Map<Integer, Set<Integer>> userLikes) {
        Set<Integer> recommended = new HashSet<>(userLikes.get(similarUserId));
        recommended.removeAll(userLikes.get(userId));
        log.debug("Found {} recommended films for user {}", recommended.size(), userId);
        return recommended;
    }

    private Set<Integer> findCommonFilmIds(List<Integer> userLikes, List<Integer> friendLikes) {
        Set<Integer> common = new HashSet<>(userLikes);
        common.retainAll(friendLikes);
        log.debug("Found {} common films", common.size());
        return common;
    }

    private List<Film> mapFilmIdsToFilms(Set<Integer> filmIds) {
        return filmIds.stream()
                .map(filmId -> filmService.getFilmById(filmId)
                        .orElseThrow(() -> new NotFoundException("Film with ID " + filmId + " not found")))
                .collect(Collectors.toList());
    }

    private void validateUserExists(int userId) {
        if (!userStorage.findUserById(userId).isPresent()) {
            log.warn("User with ID {} not found", userId);
            throw new NotFoundException("User with ID " + userId + " not found");
        }
    }
}

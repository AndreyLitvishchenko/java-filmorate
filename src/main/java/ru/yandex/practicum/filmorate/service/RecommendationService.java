package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface RecommendationService {

    List<Film> getRecommendations(int userId);

    List<Film> getCommonFilms(int userId, int friendId);
}

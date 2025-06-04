package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;
import java.util.List;

public interface ReviewService {

    Review create(Review review);

    Review update(Review review);

    void delete(int id);

    Review findReviewById(int id);

    List<Review> findAllByFilmId(Integer filmId, int count);

    void addLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void removeReaction(int reviewId, int userId);
}

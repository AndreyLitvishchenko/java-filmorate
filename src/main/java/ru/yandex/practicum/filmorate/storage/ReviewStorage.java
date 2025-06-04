package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    Optional<Review> findReviewById(int id);

    void delete(int id);

    List<Review> findAllByFilmId(Integer filmId, int count);

    void addReaction(int reviewId, int userId, boolean isLike);

    void removeReaction(int reviewId, int userId);

    void updateUseful(int reviewId);
}

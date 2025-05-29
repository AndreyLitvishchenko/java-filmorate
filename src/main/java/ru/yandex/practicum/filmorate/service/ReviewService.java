package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;

    public Review create(Review review) {
        validateReview(review);
        userStorage.findUserById(review.getUserId())
                .orElseThrow(() -> new NotFoundException("User with ID " + review.getUserId() + " not found"));
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        validateReview(review);
        findReviewById(review.getReviewId());
        return reviewStorage.update(review);
    }

    public void delete(int id) {
        findReviewById(id);
        reviewStorage.delete(id);
    }

    public Review findReviewById(int id) {
        return reviewStorage.findReviewById(id)
                .orElseThrow(() -> new NotFoundException("Review with ID " + id + " not found"));
    }

    public List<Review> findAllByFilmId(Integer filmId, int count) {
        if (filmId != null) {
            // Проверка, что фильм существует (нужно добавить FilmStorage в зависимости)
        }
        return reviewStorage.findAllByFilmId(filmId, count);
    }

    public void addLike(int reviewId, int userId) {
        validateUserExists(userId);
        findReviewById(reviewId);
        reviewStorage.addReaction(reviewId, userId, true);
    }

    public void addDislike(int reviewId, int userId) {
        validateUserExists(userId);
        findReviewById(reviewId);
        reviewStorage.addReaction(reviewId, userId, false);
    }

    public void removeReaction(int reviewId, int userId) {
        validateUserExists(userId);
        findReviewById(reviewId);
        reviewStorage.removeReaction(reviewId, userId);
    }

    private void validateReview(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Review content cannot be empty");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("isPositive cannot be null");
        }
    }

    private void validateUserExists(int userId) {
        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
    }
}

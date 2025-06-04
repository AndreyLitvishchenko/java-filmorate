package ru.yandex.practicum.filmorate.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Override
    public Review create(Review review) {
        validateReview(review);
        validateUserAndFilm(review.getUserId(), review.getFilmId());

        Review createdReview = reviewStorage.create(review);
        userService.addEvent(review.getUserId(), createdReview.getReviewId(), "REVIEW", "ADD");
        log.info("Created review: {}", createdReview);
        return createdReview;
    }

    @Override
    public Review update(Review review) {
        validateReview(review);
        findReviewById(review.getReviewId());
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        Review updatedReview = reviewStorage.update(review);
        userService.addEvent(updatedReview.getUserId(), updatedReview.getReviewId(),
                "REVIEW", "UPDATE");
        log.info("Updated review: {}", updatedReview);
        return updatedReview;
    }

    @Override
    public void delete(int id) {
        Review review = findReviewById(id);

        // Удаляем отзыв
        reviewStorage.delete(id);
        userService.addEvent(review.getUserId(), review.getReviewId(), "REVIEW", "REMOVE");
        log.info("Deleted review with ID: {}", id);
    }

    @Override
    public Review findReviewById(int id) {
        return reviewStorage.findReviewById(id)
                .orElseThrow(() -> new NotFoundException("Review with ID " + id + " not found"));
    }

    @Override
    public List<Review> findAllByFilmId(Integer filmId, int count) {
        if (filmId != null) {
            filmStorage.findFilmById(filmId)
                    .orElseThrow(() -> new NotFoundException("Film with ID " + filmId + " not found"));
        }
        return reviewStorage.findAllByFilmId(filmId, count);
    }

    @Override
    public void addLike(int reviewId, int userId) {
        validateReaction(reviewId, userId);
        reviewStorage.addReaction(reviewId, userId, true);
        log.info("User {} liked review {}", userId, reviewId);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        validateReaction(reviewId, userId);
        reviewStorage.addReaction(reviewId, userId, false);
        log.info("User {} disliked review {}", userId, reviewId);
    }

    @Override
    public void removeReaction(int reviewId, int userId) {
        validateReaction(reviewId, userId);
        reviewStorage.removeReaction(reviewId, userId);
        log.info("User {} removed reaction from review {}", userId, reviewId);
    }

    private void validateReview(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Review content cannot be empty");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("isPositive cannot be null");
        }
        if (review.getUserId() == null) {
            throw new ValidationException("userId cannot be null");
        }
        if (review.getFilmId() == null) {
            throw new ValidationException("filmId cannot be null");
        }
    }

    private void validateUserAndFilm(int userId, int filmId) {
        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Film with ID " + filmId + " not found"));
    }

    private void validateReaction(int reviewId, int userId) {
        findReviewById(reviewId);
        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
    }
}

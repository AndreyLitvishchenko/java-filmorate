package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewMapper reviewMapper;

    @Override
    public Review create(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?)",
                    new String[]{"review_id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setInt(3, review.getUserId());
            ps.setInt(4, review.getFilmId());
            return ps;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("Review created: {}", review);
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("Review with ID " + review.getReviewId() + " not found");
        }
        log.info("Review updated: {}", review);
        return findReviewById(review.getReviewId()).get();
    }

    @Override
    public Optional<Review> findReviewById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        List<Review> reviews = jdbcTemplate.query(sql, reviewMapper, id);
        return reviews.isEmpty() ? Optional.empty() : Optional.of(reviews.get(0));
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Review with ID {} deleted", id);
    }

    @Override
    public List<Review> findAllByFilmId(Integer filmId, int count) {
        String sql = filmId == null ?
                "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?" :
                "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return filmId == null ?
                jdbcTemplate.query(sql, reviewMapper, count) :
                jdbcTemplate.query(sql, reviewMapper, filmId, count);
    }

    @Override
    public void addReaction(int reviewId, int userId, boolean isLike) {
        String sql = "MERGE INTO review_reactions (review_id, user_id, is_like) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, isLike);
        log.info("User {} {} review {}", userId, isLike ? "liked" : "disliked", reviewId);
        updateUseful(reviewId);
    }

    @Override
    public void removeReaction(int reviewId, int userId) {
        String sql = "DELETE FROM review_reactions WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        log.info("User {} removed reaction from review {}", userId, reviewId);
        updateUseful(reviewId);
    }

    @Override
    public void updateUseful(int reviewId) {
        String sql = "UPDATE reviews SET useful = " +
                "(SELECT COUNT(*) FROM review_reactions WHERE review_id = ? AND is_like = true) - " +
                "(SELECT COUNT(*) FROM review_reactions WHERE review_id = ? AND is_like = false) " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId, reviewId, reviewId);
    }
}

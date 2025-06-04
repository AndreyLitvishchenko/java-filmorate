package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class Review {
    private int reviewId;
    @NotBlank(message = "Content cannot be blank")
    @Size(max = 1000, message = "Content must be less than 1000 characters")
    private String content;
    @NotNull(message = "isPositive cannot be null")
    private Boolean isPositive;
    @NotNull(message = "userId cannot be null")
    private Integer userId;
    @NotNull(message = "filmId cannot be null")
    private Integer filmId;
    private int useful = 0;
}

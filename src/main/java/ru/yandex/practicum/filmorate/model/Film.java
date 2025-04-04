package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotBlank(message = "Film name cannot be empty")
    private String name;
    @Size(max = 200, message = "Film description cannot be longer than 200 characters")
    private String description;
    @NotNull(message = "Release date cannot be null")
    private LocalDate releaseDate;
    @Positive(message = "Film duration must be positive")
    private int duration;
}
package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

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
    @NotNull
    private Mpa mpa;
    private List<Genre> genres = new ArrayList<>();

    private List<Director> directors = new ArrayList<>();

    @JsonIgnore
    private Set<Integer> likes = new HashSet<>();

    public int getLikesCount() {
        return likes.size();
    }
}

package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Director {
    private long id;

    @NotBlank(message = "Director name cannot be empty")
    private String name;
}

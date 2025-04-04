package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be a valid email address")
    private String email;
    
    @NotBlank(message = "Login cannot be empty")
    private String login;

    @NotNull(message = "Name cannot be null")
    private String name;
    
    @NotNull(message = "Birthday cannot be null")
    private LocalDate birthday;
}
package ru.yandex.practicum.filmorate.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Received request to create director: {}", director);
        return directorService.createDirector(director);
    }

    @GetMapping
    public List<Director> getDirectors() {
        log.info("Received request to get all directors");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable Long id) {
        log.info("Received request to get director with id: {}", id);
        return directorService.getDirector(id);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Received request to update director: {}", director);
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable Long id) {
        log.info("Received request to delete director with id: {}", id);
        directorService.removeDirector(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException e) {
        return Map.of("error", e.getMessage());
    }
}
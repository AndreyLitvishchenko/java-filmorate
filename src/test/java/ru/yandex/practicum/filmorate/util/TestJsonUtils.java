package ru.yandex.practicum.filmorate.util;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class TestJsonUtils {

    private TestJsonUtils() {
        // Утилитный класс
    }

    /**
     * Загружает содержимое JSON файла в виде строки
     * @param filePath путь к файлу в папке resources
     * @return содержимое файла в виде строки
     */
    public static String readJsonFromFile(String filePath) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            return new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать JSON файл: " + filePath, e);
        }
    }
}
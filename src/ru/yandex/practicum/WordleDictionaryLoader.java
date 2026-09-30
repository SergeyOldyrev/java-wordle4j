package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;

public class WordleDictionaryLoader {
    private final Logger log;

    public WordleDictionaryLoader(Logger log) {
        this.log = log;
    }

    public Set<String> loadDictionary(String resourceName) {
        Set<String> words = new HashSet<>();
        log.info("Начинаем загрузку словаря из ресурса: " + resourceName);
        InputStream inputStream = getClass().getResourceAsStream(resourceName);

        if (inputStream == null) {
            String errorMsg = "Ресурс не найден: " + resourceName + ". Проверьте, лежит ли файл в src/main/resources";
            log.log(Level.SEVERE, errorMsg);
            throw new RuntimeException(errorMsg);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String cleanWord = line.trim().toLowerCase();
                if (!cleanWord.isEmpty()) {
                    words.add(cleanWord);
                    count++;
                }
            }
            log.info("Словарь успешно загружен. Найдено слов: " + count);

        } catch (IOException e) {
            log.log(Level.SEVERE, "Ошибка чтения ресурса: " + resourceName, e);
            throw new RuntimeException("Ошибка чтения словаря", e);
        }

        return words;
    }
}
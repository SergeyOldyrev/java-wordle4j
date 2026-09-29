package ru.yandex.practicum;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {
    private final Set<String> words;

    public WordleDictionary(Set<String> words, Logger log) {
        log.info("Инициализация словаря. Нормализуем слова...");

        Set<String> normalizedWords = new HashSet<>();
        for (String word : words) {
            if (word != null) {
                normalizedWords.add(word.toLowerCase());
            } else {
                log.warning("Обнаружено null-слово в исходном наборе, пропущено.");
            }
        }
        this.words = normalizedWords;
    }

    public Set<String> getAllWords() {
        return words;
    }

    public boolean contains(String word) {
        if (word == null) {
            return false;
        }
        return words.contains(word.toLowerCase());
    }
}

package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    @Test
    void testHintExcludesForbiddenLetters() {

        Set<String> words = new HashSet<>(Arrays.asList("КОТ", "МИР", "МАК", "ТОК", "ДОМ", "МОХ"));

        WordleDictionary dictionary = new WordleDictionary(words, Logger.getLogger("test"));

        WordleGame game = new WordleGame("КОТ", dictionary, 6, Logger.getLogger("test"));

        String guess = "МОХ";
        game.makeMove(guess);

        String hint = game.getHint();
        int[] codes = game.getLastResultCodes();

        for (int i = 0; i < guess.length(); i++) {
            char currentChar = guess.charAt(i);
            int code = codes[i];

            if (code == 0) {
                assertFalse(
                        hint.contains(String.valueOf(currentChar)),
                        "Подсказка '" + hint + "' ошибочно содержит букву '" + currentChar + "', которой точно нет в слове!"
                );
            }
        }

        assertTrue(words.contains(hint));
        assertEquals(3, hint.length());
    }

    @Test
    void testHintRespectsFixedPositions() {

        Set<String> words = new HashSet<>(Arrays.asList("КОТ", "КАТ", "КОК", "РОТ", "КАР", "КИТ"));
        WordleDictionary dictionary = new WordleDictionary(words, Logger.getLogger("test"));
        WordleGame game = new WordleGame("КОТ", dictionary, 6, Logger.getLogger("test"));

        game.makeMove("КИТ");
        String hint = game.getHint();
        assertEquals('К', hint.charAt(0), "Подсказка '" + hint + "' не начинается на 'К', хотя это известно из истории");
    }

    @Test
    void testInvalidWordDoesNotSpendTurn() {

        Set<String> words = new HashSet<>(Arrays.asList("КОТ", "ДОМ", "ЛЕС"));
        WordleDictionary dictionary = new WordleDictionary(words, Logger.getLogger("test"));
        WordleGame game = new WordleGame("КОТ", dictionary, 6, Logger.getLogger("test"));

        assertThrows(WordleGame.InvalidWordException.class, () -> game.makeMove("КО"));
        assertEquals(0, game.getSteps(), "Неверный ввод не должен увеличивать счетчик ходов!");

        WordleGame.GameResult result = game.makeMove("ДОМ");
        assertEquals(1, game.getSteps(), "После правильного хода счетчик должен стать 1");
    }

    @Test
    void testEmptyInputThrowsException() {

        Set<String> words = new HashSet<>(Arrays.asList("КОТ", "МИР", "МАК", "ТОК", "ДОМ", "МОХ"));
        WordleDictionary dictionary = new WordleDictionary(words, Logger.getLogger("test"));
        WordleGame game = new WordleGame("КОТ", dictionary, 6, Logger.getLogger("test"));

        String guess = "";
        WordleGame.InvalidWordException exception = assertThrows(WordleGame.InvalidWordException.class, () -> game.makeMove(guess));

        assertTrue(exception.getMessage().contains("длина") || exception.getMessage().contains("пусто"));
    }

    @Test
    void testSpaceInputThrowsException() {

        Set<String> words = new HashSet<>(Arrays.asList("КОТ", "МИР", "МАК", "ТОК", "ДОМ", "МОХ"));
        WordleDictionary dictionary = new WordleDictionary(words, Logger.getLogger("test"));
        WordleGame game = new WordleGame("КОТ", dictionary, 6, Logger.getLogger("test"));

        String guess = "    ";
        WordleGame.InvalidWordException exception = assertThrows(WordleGame.InvalidWordException.class, () -> game.makeMove(guess));

        assertTrue(exception.getMessage().contains("длина") || exception.getMessage().contains("пусто"));
    }

    @Test
    void testTooLongInputThrowsException() {

        Set<String> words = new HashSet<>(Arrays.asList("КОТ", "МИР", "МАК", "ТОК", "ДОМ", "МОХ"));
        WordleDictionary dictionary = new WordleDictionary(words, Logger.getLogger("test"));
        WordleGame game = new WordleGame("КОТ", dictionary, 6, Logger.getLogger("test"));

        String guess = "КОТО";
        WordleGame.InvalidWordException exception = assertThrows(WordleGame.InvalidWordException.class, () -> game.makeMove(guess));

        assertTrue(exception.getMessage().contains("длина") || exception.getMessage().contains("пусто"));
    }

    @Test
    void testNotInDictionaryInputThrowsException() {
        Set<String> words = new HashSet<>(Arrays.asList("КОТ", "МИР", "МАК", "ТОК", "ДОМ", "МОХ"));
        WordleDictionary dictionary = new WordleDictionary(words, Logger.getLogger("test"));
        WordleGame game = new WordleGame("КОТ", dictionary, 6, Logger.getLogger("test"));

        String guess = "ААА";

        assertThrows(WordleGame.InvalidWordException.class, () -> game.makeMove(guess));
    }

    @Test
    void testNullInputThrowsException() {

        Set<String> words = new HashSet<>(Arrays.asList("КОТ", "МИР", "МАК", "ТОК", "ДОМ", "МОХ"));
        WordleDictionary dictionary = new WordleDictionary(words, Logger.getLogger("test"));
        WordleGame game = new WordleGame("КОТ", dictionary, 6, Logger.getLogger("test"));

        String guess = null;
        assertThrows(WordleGame.InvalidWordException.class, () -> game.makeMove(guess));
    }

    @Test
    void testLowerInputValid() {
        Set<String> words = new HashSet<>(Arrays.asList("КОТ", "МИР", "МАК", "ТОК", "ДОМ", "МОХ"));
        WordleDictionary dictionary = new WordleDictionary(words, Logger.getLogger("test"));
        WordleGame game = new WordleGame("КОТ", dictionary, 6, Logger.getLogger("test"));

        String guess = "кот";
        WordleGame.GameResult result = game.makeMove(guess);

        if (guess.toUpperCase().equals("КОТ")) {
            assertTrue(result.isWin(), "Если слово угадано, флаг победы должен быть true");
        }
    }
}

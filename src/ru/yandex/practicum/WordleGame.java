package ru.yandex.practicum;

import java.util.*;
import java.util.logging.Logger;
import ru.yandex.practicum.exceptions.InvalidWordException;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private final String answer;

    private int steps;

    private final WordleDictionary dictionary;
    private final int maxSteps;
    private final List<Move> history = new ArrayList<>();
    private boolean isGameOver = false;
    private final Logger log;
    private int[] lastResultCodes;

    public int getSteps() {
        return steps;
    }

    public int[] getLastResultCodes() {
        return lastResultCodes;
    }
    public String getAnswer() {
        return answer;
    }

    public WordleGame(String answer, WordleDictionary dictionary, int maxSteps, Logger log) {
        if (answer == null || answer.isEmpty()) {
            throw new InvalidWordException("Ответ не может быть пустым!");
        }
        if (dictionary == null) {
            throw new InvalidWordException("Словарь не может быть null!");
        }

        if (dictionary.getAllWords().isEmpty()) {
            throw new InvalidWordException("Словарь пуст, играть невозможно!");
        }
        this.log = log;
        this.answer = answer.toLowerCase();
        this.dictionary = dictionary;
        this.maxSteps = maxSteps;
        this.steps = 0;
        this.log.info("Игра началась! Секретное слово: *** (скрыто), макс. попыток: " + maxSteps);
    }

    public int[] checkGuess(String guess) {
        if (steps >= maxSteps) {
            throw new InvalidWordException("Игра окончена! Лимит попыток исчерпан.");
        }

        if (guess == null || guess.isEmpty()) {
            throw new InvalidWordException("Слово не может быть пустым.");
        }

        if (guess.length() != answer.length()) {
            throw new InvalidWordException(
                    "Неверная длина слова! Ожидалось " + answer.length() + " букв."
            );
        }

        if (!dictionary.contains(guess.toLowerCase())) {
            throw new InvalidWordException("Такого слова нет в словаре!");
        }

        String lowerGuess = guess.toLowerCase();
        int[] result = new int[answer.length()];
        boolean[] usedInAnswer = new boolean[answer.length()];

        for (int i = 0; i < answer.length(); i++) {
            if (lowerGuess.charAt(i) == answer.charAt(i)) {
                result[i] = 2;
                usedInAnswer[i] = true;
            }
        }

        for (int i = 0; i < lowerGuess.length(); i++) {
            if (result[i] == 2) {
                continue;
            }

            char currentChar = lowerGuess.charAt(i);

            for (int j = 0; j < answer.length(); j++) {
                if (answer.charAt(j) == currentChar && !usedInAnswer[j]) {
                    result[i] = 1;
                    usedInAnswer[j] = true;
                    break;
                }
            }
        }

        return result;
    }

    public static class GameResult {
        private final boolean isWin;
        private final boolean isGameOver;
        private final int[] colors;
        private final int remainingSteps;

        public GameResult(boolean isWin, boolean isGameOver, int[] colors, int remainingSteps) {
            this.isWin = isWin;
            this.isGameOver = isGameOver;
            this.colors = colors;
            this.remainingSteps = remainingSteps;
        }

        public boolean isWin() {
            return isWin;
        }

        public boolean isGameOver() {
            return isGameOver;
        }

        public int[] getColors() {
            return colors;
        }

        public int getRemainingSteps() {
            return remainingSteps;
        }
    }

    public GameResult makeMove(String guess) {
        if (guess == null) {
            throw new InvalidWordException("Ввод не может быть пустым. Пожалуйста, введите слово.");
        }
        if (guess.length() != answer.length()) {
            throw new InvalidWordException("Ошибка: Недопустимая длина слова");
        }

        if (this.isGameOver) {
            throw new InvalidWordException("Игра уже окончена! Нельзя делать новые ходы.");
        }

        if (steps >= maxSteps) {
            throw new InvalidWordException("Игра окончена! Лимит попыток исчерпан.");
        }

        int[] colors = checkGuess(guess);

        steps++;
        history.add(new Move(guess.toLowerCase(), colors));
        this.log.fine("Ход #" + steps + ". Игрок ввел: " + guess + ". Результат цветов: " + Arrays.toString(colors));

        boolean isWin = Arrays.stream(colors).allMatch(code -> code == 2);
        log.info("=== ОТЛАДКА ХОДА ===");
        log.info("Загаданное слово: " + this.answer);
        log.info("Введённое слово: " + guess);
        log.info("Массив кодов (resultCodes): " + java.util.Arrays.toString(colors));
        log.info("===================");
        this.lastResultCodes = colors;
        boolean currentRoundIsOver = isWin || (steps >= maxSteps);

        this.isGameOver = currentRoundIsOver;

        int remainingSteps = maxSteps - steps;

        return new GameResult(isWin, currentRoundIsOver, colors, remainingSteps);
    }

    private static class Move {
        String word;
        int[] resultCodes;

        public Move(String word, int[] resultCodes) {
            this.word = word;
            this.resultCodes = resultCodes;
        }
    }

    public String getHint() {
        log.info("Всего ходов сделано: " + history.size());

        Set<Character> forbiddenLetters = new HashSet<>();
        Map<Integer, Character> fixedLetters = new HashMap<>();

        for (Move move : history) {
            String word = move.word;
            int[] codes = move.resultCodes;

            for (int i = 0; i < word.length(); i++) {
                char letter = word.charAt(i);
                int code = codes[i];

                if (code == 0) {
                    forbiddenLetters.add(letter);
                } else if (code == 2) {
                    fixedLetters.put(i, letter);
                }
            }
        }

        for (String candidate : dictionary.getAllWords()) {

            if (candidate.length() != answer.length()) {
                continue;
            }

            boolean hasForbidden = false;

            for (char c : forbiddenLetters) {
                if (candidate.contains(String.valueOf(c))) {
                    hasForbidden = true;
                    break;
                }
            }
            if (hasForbidden) {
                continue;
            }

            boolean matchesFixed = true;
            for (Map.Entry<Integer, Character> entry : fixedLetters.entrySet()) {
                int index = entry.getKey();
                char requiredChar = entry.getValue();

                if (candidate.charAt(index) != requiredChar) {
                    matchesFixed = false;
                    break;
                }
            }

            if (matchesFixed) {
                log.info("Подсказка: " + candidate);
                return candidate.toUpperCase();
            }
        }

        return dictionary.getAllWords().iterator().next();
    }
}
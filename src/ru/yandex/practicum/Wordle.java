package ru.yandex.practicum;

import java.util.*;
import java.util.logging.Logger;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */

public class Wordle {
    private static final Logger log = Logger.getLogger(Wordle.class.getName());

    public static void main(String[] args) {

        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        Set<String> rawWords = loader.loadDictionary("words_ru.txt");
        WordleDictionary dictionary = new WordleDictionary(rawWords,log);

        Scanner scanner = new Scanner(System.in);
        List<String> wordList = new ArrayList<>(dictionary.getAllWords());

        if (wordList.isEmpty()) {
            System.out.println("Ошибка: Словарь пуст! Невозможно начать игру.");
            return;
        }

        Random random = new Random();
        String randomAnswer = wordList.get(random.nextInt(wordList.size()));

        System.out.println(" Слово загадано (длина: " + randomAnswer.length() + " букв). Удачи!");

        WordleGame game = new WordleGame(randomAnswer, dictionary, 6,log);
    
        log.info("Игра запущена. Загадано слово из " + randomAnswer.length() + " букв.");

        while (true) {
            System.out.print("Твой ход (или 'hint' для подсказки): ");
            String guess = scanner.nextLine().trim();

            if ("hint".equals(guess) || "?".equals(guess)) {
                String hint = game.getHint();
                System.out.println("💡 Подсказка: попробуй слово — " + hint);

                continue;
            }

            try {
                WordleGame.GameResult result = game.makeMove(guess);

                printColors(result.getColors());

                if (result.isWin()) {
                    System.out.println(" Поздравляю! Ты угадал слово!");
                    break;
                }

                if (result.isGameOver()) {
                    System.out.println(" Игра окончена. Ты не угадал слово. Ответ был: " + game.getAnswer());
                    break;
                }

                System.out.println("Осталось ходов: " + result.getRemainingSteps());

            } catch (WordleGame.InvalidWordException e) {
                System.out.println(" Ошибка ввода: " + e.getMessage());
                System.out.println("Попробуй ещё раз (ход не потрачен!).");
            } catch (WordleGame.GameStateException e) {
                System.out.println(" Состояние игры: " + e.getMessage());
                break;
            }
        }
    }


    private static void printColors(int[] colors) {
        StringBuilder sb = new StringBuilder();
        for (int code : colors) {
            if (code == 2) sb.append("🟩"); // Зелёный
            else if (code == 1) sb.append("🟨"); // Жёлтый
            else sb.append("⬜"); // Серый
        }
        System.out.println(sb);
    }
}